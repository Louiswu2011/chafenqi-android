package com.nltv.chafenqi.view.login

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beust.klaxon.Klaxon
import com.nltv.chafenqi.ChafenqiApp
import com.nltv.chafenqi.ChafenqiApplication
import com.nltv.chafenqi.UIState
import com.nltv.chafenqi.extension.sha256
import com.nltv.chafenqi.networking.CFQServer
import com.nltv.chafenqi.networking.CFQServerSideException
import com.nltv.chafenqi.networking.CredentialsMismatchException
import com.nltv.chafenqi.networking.EmptyUserDataException
import com.nltv.chafenqi.networking.FishServer
import com.nltv.chafenqi.storage.CFQUser
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiUserInfo
import com.nltv.chafenqi.storage.room.songlist.chunithm.ChunithmMusicEntry
import com.nltv.chafenqi.storage.room.songlist.chunithm.ChunithmMusicListRepository
import com.nltv.chafenqi.storage.room.songlist.maimai.MaimaiMusicEntry
import com.nltv.chafenqi.storage.room.songlist.maimai.MaimaiMusicListDatabase
import com.nltv.chafenqi.storage.room.songlist.maimai.MaimaiMusicListRepository
import com.nltv.chafenqi.storage.room.user.maimai.LocalUserMaimaiDataRepository
import com.nltv.chafenqi.storage.room.user.maimai.MaimaiBestScoreEntry
import com.nltv.chafenqi.storage.room.user.maimai.MaimaiRecentScoreEntry
import com.nltv.chafenqi.storage.room.user.maimai.UserMaimaiDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginPageViewModel(
    private val maiListRepo: MaimaiMusicListRepository,
    private val chuListRepo: ChunithmMusicListRepository,
    private val userMaiDataRepo: UserMaimaiDataRepository
): ViewModel() {
    data class LoginUiState(
        val loginState: UIState = UIState.Pending,
        val loginPromptText: String = ""
    )

    private var loginState = MutableStateFlow(LoginUiState())
    private var user by mutableStateOf(CFQUser)

    var loginUiState = loginState.asStateFlow()


    fun login(username: String, passwordHash: String) {
        updateLoginState(UIState.Loading)
        updateLoginPromptText("登陆中...")

        viewModelScope.launch {
            try {
                Log.i("Login", "Debug sha256 output: $passwordHash")
                val response = CFQServer.authLogin(
                    username = username,
                    password = passwordHash
                )

                if (response.isNotEmpty()) {
                    // successfully logged in
                    println("Successfully logged in.")
                    updateLoginPromptText("以${username}的身份登录...")
                    user.createProfile(response, username)

                    // loadPersistentStorage()

                    loadMaimaiData()

                    // updateLoginState(UIState.Finished)
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

    private suspend fun loadPersistentStorage() {
        val parser = Klaxon()

        updateLoginPromptText("加载歌曲列表...")
        val maiListData = FishServer.fetchMaimaiMusicListData()
        Log.i("Login","Got maimai music list, size ${maiListData.length}")

        val chuListData = CFQServer.apiChuithmMusicData()
        Log.i("Login","Got chunithm music list, size ${chuListData.length}")


        withContext(Dispatchers.IO) {
            parser.parseArray<MaimaiMusicEntry>(maiListData)?.also {
                if (it.size != maiListRepo.getMusicCount()) {
                    updateMaiList(it)
                    Log.i("Login", "Saved maimai music list.")
                } else {
                    Log.i("Login", "Maimai music list cache is up-to-date, skipping...")
                }
            } ?: run {
                // parse failed
                Log.e("Login", "Maimai list parse failed.")
            }

            parser.parseArray<ChunithmMusicEntry>(chuListData)?.also {
                if (it.size != chuListRepo.getMusicCount()) {
                    updateChuList(it)
                    Log.i("Login", "Saved chunithm music list.")
                } else {
                    Log.i("Login", "Chunithm music list cache is up-to-date, skipping...")
                }
            } ?: run {
                Log.e("Login", "Chunithm list parse failed.")
            }
        }
    }

    private suspend fun loadMaimaiData() {
        val tag = "Login.User.MaimaiData"
        val parser = Klaxon()
        val token = user.token

        var isEmpty = false

        updateLoginPromptText("加载舞萌DX数据...")

        withContext(Dispatchers.IO) {
            try {
                Log.i(tag, "Saving maimai data...")

                val infoString = CFQServer.apiMaimai("info", token)
                val bestString = CFQServer.apiMaimai("best", token)
                val recentString = CFQServer.apiMaimai("recent", token)
                Log.i(tag, "Data size: ${infoString.length}, ${bestString.length}, ${recentString.length}.")

                val info = parser.parse<MaimaiUserInfo>(infoString)
                val best = parser.parseArray<MaimaiBestScoreEntry>(bestString)
                val recent = parser.parseArray<MaimaiRecentScoreEntry>(recentString)

                Log.i(tag, "Maimai data acquired.")
                best?.onEach { userMaiDataRepo.rawDao.upsertBestScore(it) } ?: run {
                    Log.e(
                        tag,
                        "Failed to parse best scores."
                    )
                }
                recent?.onEach { userMaiDataRepo.rawDao.insertRecentScore(it) } ?: run {
                    Log.e(
                        tag,
                        "Failed to parse recent scores."
                    )
                }

                Log.i(tag, "Finished on loading user maimai basic data.")
            } catch (e: EmptyUserDataException) {
                isEmpty = true
                Log.i(tag, "User maimai data is empty, skipping...")
            } catch (e: Exception) {
                Log.e(tag, e.localizedMessage)
            }

            if (CFQUser.isPremium && !isEmpty) {
                Log.i(tag, "Fetching premium related data...")
            }
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