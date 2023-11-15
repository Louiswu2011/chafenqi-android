package com.nltv.chafenqi.view.login

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nltv.chafenqi.CFQUserStateViewModel
import com.nltv.chafenqi.UIState
import com.nltv.chafenqi.networking.CFQServer
import com.nltv.chafenqi.storage.CFQUser
import com.nltv.chafenqi.storage.`object`.CFQPersistentData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginPageViewModel(

) : ViewModel() {
    data class LoginUiState(
        val loginState: UIState = UIState.Pending,
        val loginPromptText: String = ""
    )

    private var loginState = MutableStateFlow(LoginUiState())
    private var user by mutableStateOf(CFQUser)

    var loginUiState = loginState.asStateFlow()

    fun loginWithToken(token: String) {

    }

    fun login(
        username: String,
        passwordHash: String,
        context: Context,
        userState: CFQUserStateViewModel
    ) {
        updateLoginState(UIState.Loading)
        updateLoginPromptText("登陆中...")
        viewModelScope.launch {
            try {
                val response = CFQServer.authLogin(
                    username = username,
                    password = passwordHash
                )

                if (response.isNotEmpty()) {
                    // successfully logged in
                    println("Successfully logged in.")
                    // updateLoginPromptText("以${username}的身份登录...")
                    user.createProfile(response, username)


                    loadPersistentStorage(context)

                    updateLoginPromptText("加载舞萌DX数据...")
                    userState.loadMaimaiData()

                    updateLoginPromptText("加载中二节奏数据...")
                    userState.loadChunithmData()

                    updateLoginState(UIState.Pending)
                    userState.isLoggedIn = true
                } else {
                    updateLoginState(UIState.Pending)
                }
            } catch (e: Exception) {
                updateLoginState(UIState.Pending)
                throw e
            }
        }
    }

    fun register(username: String, passwordHash: String) {

    }

    private suspend fun loadPersistentStorage(context: Context) {
        updateLoginPromptText("加载歌曲列表...")
        CFQPersistentData.loadData(shouldValidate = false, context = context)
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

}