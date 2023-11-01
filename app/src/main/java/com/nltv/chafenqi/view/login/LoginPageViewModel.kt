package com.nltv.chafenqi.view.login

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nltv.chafenqi.UIState
import com.nltv.chafenqi.networking.CFQServer
import com.nltv.chafenqi.networking.CFQServerSideException
import com.nltv.chafenqi.networking.CredentialsMismatchException
import com.nltv.chafenqi.storage.CFQUser
import com.nltv.chafenqi.storage.`object`.CFQPersistentData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class LoginPageViewModel(

): ViewModel() {
    data class LoginUiState(
        val loginState: UIState = UIState.Pending,
        val loginPromptText: String = ""
    )

    private var loginState = MutableStateFlow(LoginUiState())
    private var user by mutableStateOf(CFQUser)

    var loginUiState = loginState.asStateFlow()


    fun login(username: String, passwordHash: String, context: Context) {
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
                    loadMaimaiData()

                    updateLoginPromptText("加载中二节奏数据...")
                    loadChunithmData()

                    updateLoginState(UIState.Finished)
                } else {
                    updateLoginState(UIState.Pending)
                }
            } catch (e: CredentialsMismatchException) {
                Log.e("Login", "Login failed: Credentials mismatched.")
                updateLoginState(UIState.Pending)
            } catch (e: CFQServerSideException) {
                Log.e("Login", "Server side error: ${e.message}")
                updateLoginState(UIState.Pending)
            }
        }
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

    private suspend fun loadMaimaiData() {
        val tag = "Login.User.MaimaiData"
        val token = user.token

        val maimai = CFQUser.Maimai

        var isEmpty = false

        withContext(Dispatchers.IO) {
            try {
                val infoString = CFQServer.apiMaimai("info", token)
                val bestString = CFQServer.apiMaimai("best", token)
                val recentString = CFQServer.apiMaimai("recent", token)

                maimai.info = Json.decodeFromString(infoString)
                maimai.best = Json.decodeFromString(bestString)
                maimai.recent = Json.decodeFromString(recentString)

                maimai.addAuxiliaryData()
                Log.i(tag, "Loaded user maimai basic data.")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(tag, "User maimai data is empty, skipping...")
                isEmpty = true
            }

            if (user.isPremium && !isEmpty) {
                try {
                    val deltaString = CFQServer.apiMaimai("delta", token)
                    val extraString = CFQServer.apiMaimai("extra", token)

                    maimai.delta = Json.decodeFromString(deltaString)
                    maimai.extra = Json.decodeFromString(extraString)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(tag, "Error loading user maimai premium data.")
                }
            }
        }
    }

    private suspend fun loadChunithmData() {
        val tag = "Login.User.ChunithmData"
        val token = user.token
        val deserializer = Json { ignoreUnknownKeys = true }

        val chunithm = CFQUser.Chunithm

        var isEmpty = false

        withContext(Dispatchers.IO) {
            try {
                val infoString = CFQServer.apiChunithm("info", token)
                val bestString = CFQServer.apiChunithm("best", token)
                val recentString = CFQServer.apiChunithm("recent", token)
                val ratingString = CFQServer.apiChunithm("rating", token)

                chunithm.info = deserializer.decodeFromString(infoString)
                chunithm.best = deserializer.decodeFromString(bestString)
                chunithm.recent = deserializer.decodeFromString(recentString)
                chunithm.rating = deserializer.decodeFromString(ratingString)
                Log.i(tag, "Loaded user chunithm basic data.")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(tag, "User chunithm data is empty, skipping...")
                isEmpty = true
            }

            if (user.isPremium && !isEmpty) {
                try {
                    val deltaString = CFQServer.apiChunithm("delta", token)
                    val extraString = CFQServer.apiChunithm("extra", token)

                    chunithm.delta = deserializer.decodeFromString(deltaString)
                    chunithm.extra = deserializer.decodeFromString(extraString)
                    Log.i(tag, "Loaded user chunithm premium data.")
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(tag, "Error loading user chunithm premium data.")
                }
            }
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