package com.nltv.chafenqi.view

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beust.klaxon.Klaxon
import com.nltv.chafenqi.UIState
import com.nltv.chafenqi.extension.sha256
import com.nltv.chafenqi.networking.CFQServer
import com.nltv.chafenqi.networking.CredentialsMismatchException
import com.nltv.chafenqi.networking.FishServer
import com.nltv.chafenqi.storage.CFQUser
import com.nltv.chafenqi.storage.room.chunithm.ChunithmMusicEntry
import com.nltv.chafenqi.storage.room.chunithm.ChunithmMusicListRepository
import com.nltv.chafenqi.storage.room.maimai.MaimaiMusicEntry
import com.nltv.chafenqi.storage.room.maimai.MaimaiMusicListRepository
import kotlinx.coroutines.launch

class LoginPageViewModel(
    private val maiListRepo: MaimaiMusicListRepository,
    private val chuListRepo: ChunithmMusicListRepository
): ViewModel() {
    var loginState by mutableStateOf(UIState.Pending)
    private var user by mutableStateOf(CFQUser)

    var loginPromptText by mutableStateOf("")

    fun login(username: String, password: String): Boolean {
        var loggedIn = false
        loginState = UIState.Loading
        loginPromptText = "登陆中..."

        viewModelScope.launch {
            try {
                Log.i("Login", "Debug sha256 output: ${password.sha256()}")
                val response = CFQServer.authLogin(
                    username = username,
                    password = password.sha256()
                )

                if (response.isNotEmpty()) {
                    // successfully logged in
                    println("Successfully logged in.")
                    loginPromptText = "以${username}的身份登录..."
                    user.createProfile(response, username)
                    // loadPersistentStorage()

                    loggedIn = true
                }
            } catch (e: CredentialsMismatchException) {
                Log.e("Login", "Login failed: Credentials mismatched.")
            }
        }

        return loggedIn
    }

    private suspend fun loadPersistentStorage() {
        val parser = Klaxon()

        loginPromptText = "加载歌曲列表..."
        val maiListData = FishServer.fetchMaimaiMusicListData()
        Log.i("Login","Got maimai music list, size ${maiListData.length}")

        val chuListData = CFQServer.apiChuithmMusicData()
        Log.i("Login","Got chunithm music list, size ${chuListData.length}")

        val maiList = parser.parseArray<MaimaiMusicEntry>(maiListData)
        val chuList = parser.parseArray<ChunithmMusicEntry>(chuListData)

        maiList?.also {
            Log.i("Login","Saving maimai music list.")
            updateMaiList(it)
            Log.i("Login","Saved maimai music list.")
        } ?: run {
            // parse failed
            Log.e("Login", "Maimai list parse failed.")
        }

        chuList?.also {
            Log.i("Login","Saving chunithm music list.")
            updateChuList(it)
            Log.i("Login","Saved chunithm music list.")
        } ?: run {
            Log.e("Login", "Chunithm list parse failed.")
        }
    }

    private suspend fun updateMaiList(list: List<MaimaiMusicEntry>) {
        list.onEach {
            maiListRepo.insertMusic(it)
        }
    }

    private suspend fun updateChuList(list: List<ChunithmMusicEntry>) {
        list.onEach {
            chuListRepo.insertMusic(it)
        }
    }

}