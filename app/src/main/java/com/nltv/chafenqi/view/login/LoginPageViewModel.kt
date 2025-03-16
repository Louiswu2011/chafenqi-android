package com.nltv.chafenqi.view.login

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.perf.metrics.AddTrace
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.nltv.chafenqi.CFQUserStateViewModel
import com.nltv.chafenqi.UIState
import com.nltv.chafenqi.cacheStore
import com.nltv.chafenqi.networking.CFQServer
import com.nltv.chafenqi.networking.CFQServerSideException
import com.nltv.chafenqi.networking.CredentialsMismatchException
import com.nltv.chafenqi.networking.UserNotFoundException
import com.nltv.chafenqi.storage.persistent.CFQPersistentData
import com.nltv.chafenqi.storage.user.CFQUser
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.ConnectException

class LoginPageViewModel : ViewModel() {
    data class LoginUiState(
        val loginState: UIState = UIState.Pending,
        val loginPromptText: String = ""
    )

    private var loginState = MutableStateFlow(LoginUiState())
    var user by mutableStateOf(CFQUser)

    var loginUiState = loginState.asStateFlow()

    @AddTrace(name = "login_cached")
    @Suppress("UNUSED_PARAMETER")
    fun login(
        token: String,
        username: String,
        context: Context,
        shouldValidate: Boolean = true,
        userState: CFQUserStateViewModel,
        snackbarHostState: SnackbarHostState,
        loadFromCache: Boolean = true
    ) {
        updateLoginState(UIState.Loading)
        updateLoginPromptText("登陆中...")

        viewModelScope.launch {
            try {
                user.createProfile(token, username)

                loadPersistentStorage(context, shouldValidate)

                updateLoginPromptText("加载舞萌DX数据...")
                userState.loadMaimaiData(context)

                updateLoginPromptText("加载中二节奏数据...")
                userState.loadChunithmData(context)

                updateLoginState(UIState.Pending)
                userState.isLoggedIn = true
            } catch (e: Exception) {
                loginExceptionHandler(context, snackbarHostState, e)
            }
        }

    }

    @AddTrace(name = "login_fresh")
    fun login(
        username: String,
        passwordHash: String,
        context: Context,
        shouldValidate: Boolean = true,
        userState: CFQUserStateViewModel,
        snackbarHostState: SnackbarHostState
    ) {
        updateLoginState(UIState.Loading)
        updateLoginPromptText("登陆中...")
        viewModelScope.launch {
            try {
                val response = CFQServer.authLogin(
                    username = username,
                    password = passwordHash
                )
                val (status, token) = response

                if (status != HttpStatusCode.OK) {
                    updateLoginState(UIState.Pending)
                    snackbarHostState.showSnackbar("用户名或密码错误，请重试")
                } else if (token.isNotEmpty()) {
                    // successfully logged in
                    println("Successfully logged in.")
                    // updateLoginPromptText("以${username}的身份登录...")
                    user.createProfile(token, username)

                    loadPersistentStorage(context, shouldValidate)

                    updateLoginPromptText("加载舞萌DX数据...")
                    userState.loadMaimaiData(context)

                    updateLoginPromptText("加载中二节奏数据...")
                    userState.loadChunithmData(context)
                    updateLoginState(UIState.Pending)
                    userState.isLoggedIn = true
                } else {
                    updateLoginState(UIState.Pending)
                    snackbarHostState.showSnackbar("未知错误，请重试")
                }
            } catch (e: Exception) {
                loginExceptionHandler(context, snackbarHostState, e)
            }
        }
    }

    private suspend fun loginExceptionHandler(context: Context, snackbarHostState: SnackbarHostState, e: Exception) {
        updateLoginState(UIState.Pending)
        Log.e("LoginPageViewModel", "$e ${e::class.simpleName}")
        val result = when (e) {
            is UserNotFoundException, is CredentialsMismatchException -> {
                snackbarHostState.showSnackbar("用户名或密码错误",
                    withDismissAction = true,
                    actionLabel = "复制日志")
            }

            is CFQServerSideException -> {
                snackbarHostState.showSnackbar("服务器出错，请稍后再试",
                    withDismissAction = true,
                    actionLabel = "复制日志")
            }

            is ConnectException -> {
                snackbarHostState.showSnackbar("无法连接到服务器，请稍后再试",
                    withDismissAction = true,
                    actionLabel = "复制日志")
            }

            else -> {
                snackbarHostState.showSnackbar(
                    message = "未知错误: ${e.localizedMessage}",
                    withDismissAction = true,
                    actionLabel = "复制日志"
                )
            }
        }
        if (result == SnackbarResult.ActionPerformed) {
            try {
                val clipboardManager =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText(
                    "Exceptions",
                    "${e.message}\n${e.stackTraceToString()}"
                )
                clipboardManager.setPrimaryClip(clipData)
            } catch (e: Exception) {
                snackbarHostState.showSnackbar("复制日志失败")
            }
        }
        Firebase.crashlytics.apply { 
            setCustomKey("loginException", e::class.simpleName ?: "Unknown")
            setCustomKey("location", "LoginPageViewModel")
            recordException(e)
        }
    }

    private suspend fun loadPersistentStorage(context: Context, shouldValidate: Boolean) {
        updateLoginPromptText("加载歌曲列表...")
        CFQPersistentData.loadData(shouldValidate = shouldValidate, context = context)
    }

    fun clearPersistentStorage(context: Context) {
        viewModelScope.launch {
            CFQPersistentData.clearData(context)
        }
    }


    private fun updateLoginPromptText(newText: String) {
        loginState.update { state ->
            state.copy(
                loginState = state.loginState,
                loginPromptText = newText
            )
        }
    }

    private fun updateLoginState(newState: UIState) {
        loginState.update { state ->
            state.copy(
                loginState = newState,
                loginPromptText = state.loginPromptText
            )
        }
    }

    suspend fun getCachedCredentials(context: Context): List<String> {
        val store = context.cacheStore
        val tokenKey = stringPreferencesKey("cachedToken")
        val usernameKey = stringPreferencesKey("cachedUsername")

        return listOf(
            store.data.map { it[tokenKey] }.first() ?: "",
            store.data.map { it[usernameKey] }.first() ?: ""
        )
    }

}