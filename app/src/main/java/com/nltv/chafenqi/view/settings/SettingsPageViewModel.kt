package com.nltv.chafenqi.view.settings

import android.app.StatusBarManager
import android.content.ComponentName
import android.content.Context
import android.graphics.drawable.Icon
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.annotation.ExperimentalCoilApi
import coil.imageLoader
import com.nltv.chafenqi.BuildConfig
import com.nltv.chafenqi.R
import com.nltv.chafenqi.cacheStore
import com.nltv.chafenqi.networking.CFQServer
import com.nltv.chafenqi.storage.persistent.CFQPersistentData
import com.nltv.chafenqi.storage.user.CFQUser
import com.nltv.chafenqi.tile.UpdaterTileService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime
import java.util.concurrent.Executor

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
    var showClearCacheAlert by mutableStateOf(false)

    var isReloadingList by mutableStateOf(false)
    var diskCacheSize by mutableStateOf("")

    val user = CFQUser
    val username = user.username
    val token = user.token
    val bindQQ = user.remoteOptions.bindQQ

    var maiSongListVersionString by mutableStateOf("")
    var chuSongListVersionString by mutableStateOf("")

    suspend fun isAppVersionLatest(): Boolean {
        val versionData = CFQServer.apiFetchLatestVersion()
        val fullVersionString = BuildConfig.VERSION_NAME
        val versionCode = fullVersionString.split(" ")[0]
        val buildNumber = fullVersionString.split(" ")[1]
            .removePrefix("(")
            .removeSuffix(")")
            .toInt()
        return versionData.isLatest(versionCode, buildNumber)
    }

    @OptIn(FormatStringsInDatetimeFormats::class)
    fun updateSongListVersion() {
        viewModelScope.launch {
            maiSongListVersionString = kotlinx.datetime.Instant.fromEpochSeconds(CFQPersistentData.Maimai.version.toLong())
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .format(LocalDateTime.Format {
                    byUnicodePattern("yyyy-MM-dd")
                })
            chuSongListVersionString = kotlinx.datetime.Instant.fromEpochSeconds(CFQPersistentData.Chunithm.version.toLong())
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .format(LocalDateTime.Format {
                    byUnicodePattern("yyyy-MM-dd")
                })
        }
    }

    fun updateSponsorList() {
        viewModelScope.launch {
            _uiState.update { currentValue ->
                currentValue.copy(
                    sponsorList = CFQServer.statSponsorList()
                )
            }
        }
    }

    @OptIn(FormatStringsInDatetimeFormats::class)
    fun updateUserPremiumTime() {
        viewModelScope.launch {
            val time = CFQServer.apiCheckPremiumTime(username)
            val nowInstant = Clock.System.now()
            val premiumInstant = Instant.fromEpochSeconds(time.toLong())
            val dateString = premiumInstant
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .format(LocalDateTime.Format { byUnicodePattern("yyyy-MM-dd") })

            val statusString = if (premiumInstant > nowInstant) {
                "有效期至$dateString"
            } else {
                "已于${dateString}过期"
            }

            _uiState.update { currentValue ->
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

    @OptIn(ExperimentalCoilApi::class)
    fun clearCoilCache(context: Context) {
        val imageLoader = context.imageLoader
        val diskCache = imageLoader.diskCache
        val memoryCache = imageLoader.memoryCache

        diskCache?.clear()
        memoryCache?.clear()
        getCoilDiskCacheSize(context)
    }

    @OptIn(ExperimentalCoilApi::class)
    fun getCoilDiskCacheSize(context: Context) {
        val diskCache = context.imageLoader.diskCache
        diskCacheSize = when (val sizeInBytes = diskCache?.size ?: 0) {
            in 1..1024 -> {
                "${sizeInBytes}B"
            }

            in 1025..1024 * 1024 -> {
                "${String.format("%.2f", sizeInBytes / 1024f)}KB"
            }

            in 1024 * 1024 + 1..1024 * 1024 * 1024 -> {
                "${String.format("%.2f", sizeInBytes / 1024f / 1024f)}MB"
            }

            in 1024 * 1024 * 1024 + 1..Long.MAX_VALUE -> {
                "${String.format("%.2f", sizeInBytes / 1024f / 1024f / 1024f)}GB"
            }

            else -> {
                ""
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestAddTile(context: Context) {
        val statusBarManager = context.getSystemService(StatusBarManager::class.java)
        statusBarManager.requestAddTileService(
            ComponentName(context, UpdaterTileService::class.java),
            "传分代理",
            Icon.createWithResource(context, R.drawable.tile_upload_icon),
            Executor { }
        ) { }
    }

}