package com.nltv.chafenqi.view.settings

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nltv.chafenqi.cacheStore
import com.nltv.chafenqi.networking.CFQServer
import com.nltv.chafenqi.storage.CFQUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class SettingsUiState(
    val sponsorList: List<String> = listOf(),
    val membershipStatus: String = ""
)
data class DeveloperInfo(
    val name: String = "",
    val contribution: String = ""
)

val DEVELOPERS = listOf(
    DeveloperInfo("louiswu2011", "主程序"),
    DeveloperInfo("0Shu", "美术支持"),
    DeveloperInfo("SoreHait", "技术支持"),
    DeveloperInfo("Diving-Fish", "舞萌DX数据支持"),
    DeveloperInfo("bakapiano", "国服代理传分方案"),
    DeveloperInfo("sdvx.in", "中二节奏谱面数据")
)

val GAME_LIST = listOf(
    "中二节奏NEW",
    "舞萌DX"
)

class SettingsPageViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    var showLogoutAlert by mutableStateOf(false)
    var showReloadListAlert by mutableStateOf(false)

    var isReloadingList by mutableStateOf(false)

    val user = CFQUser
    val username = user.username
    val token = user.token

    fun updateSponsorList() {
        viewModelScope.launch {
            _uiState.update {  currentValue ->
                currentValue.copy(
                    sponsorList = CFQServer.statSponsorList()
                )
            }
        }
    }

    fun updateUserPremiumTime() {
        viewModelScope.launch {
            val time = CFQServer.apiCheckPremiumTime(username)
            val nowInstant = Instant.now()
            val premiumInstant = Instant.ofEpochMilli(time.toLong() * 1000)
            val dateString = premiumInstant
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            val statusString = if (premiumInstant.isAfter(nowInstant)) {
                "有效期至$dateString"
            } else {
                "已于${dateString}过期"
            }

            _uiState.update {  currentValue ->
                currentValue.copy(
                    membershipStatus = statusString
                )
            }
        }
    }

    suspend fun clearCachedCredentials(context: Context): Boolean {
        val store = context.cacheStore
        val tokenKey = stringPreferencesKey("cachedToken")
        val usernameKey = stringPreferencesKey("cachedUsername")

        return try {
            store.edit {
                it[tokenKey] = ""
                it[usernameKey] = ""
            }
            true
        } catch (e: Exception) {
            Log.e("SettingsPageViewModel", "Failed to save credentials to cache.")
            false
        }
    }

}