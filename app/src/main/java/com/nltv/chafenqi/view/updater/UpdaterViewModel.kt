package com.nltv.chafenqi.view.updater

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.VpnService
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.UriHandler
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.nltv.chafenqi.networking.CFQServer
import com.nltv.chafenqi.networking.FishServer
import com.nltv.chafenqi.storage.user.CFQUser
import com.nltv.chafenqi.updater.ChafenqiProxy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

val PORTAL_ADDRESS = "http://43.139.107.206:8083/"

data class UpdaterUiState(
    val maiServerStat: String = "暂无数据",
    val chuServerStat: String = "暂无数据",
    val maiUploadStat: String = "暂未上传",
    val chuUploadStat: String = "暂未上传",
    val maiTokenCacheStat: String = "无缓存",
    val chuTokenCacheStat: String = "无缓存",
    val canPerformMaiQuickUpload: Boolean = false,
    val canPerformChuQuickUpload: Boolean = false
)

data class UpdaterHelpInfo(
    val icon: ImageVector = Icons.Default.ArrowDropDown,
    val contentDescription: String = "",
    val title: String = "",
    val text: String = ""
)

val HELPS = listOf(
    UpdaterHelpInfo(
        Icons.Default.Link,
        "第一步",
        "复制链接",
        "复制需要传分的游戏链接，打开微信发送到任意聊天内"
    ),
    UpdaterHelpInfo(
        Icons.Default.ToggleOn,
        "第二步",
        "打开代理开关",
        "第一次打开需要同意应用的VPN使用请求，开启期间无法使用网络属正常现象"
    ),
    UpdaterHelpInfo(
        Icons.Default.AdsClick,
        "第三步",
        "点击链接",
        "回到微信，点击发送的链接，并等待弹出“上传中”的提示"
    ),
    UpdaterHelpInfo(
        Icons.Default.Timelapse,
        "第四步",
        "等待上传完成",
        "返回到传分页面中，等待传分状态从“正在上传”变为“未开始上传”，即可回到主页下拉刷新数据"
    ),
    UpdaterHelpInfo(
        Icons.Default.Info,
        "关于水鱼网",
        "同步至水鱼网",
        "如需将分数同步上传至水鱼网，请先在主页设置中绑定水鱼网账号，再打开“同步至水鱼网”开关，最后复制新的链接并上传即可"
    )
)

class UpdaterViewModel : ViewModel() {
    val token = CFQUser.token
    private val _uiState = MutableStateFlow(UpdaterUiState())
    val uiState: StateFlow<UpdaterUiState> = _uiState.asStateFlow()

    var shouldShowQRCode by mutableStateOf(false)

    fun updateServerStat() {
        fun makeServerStatText(time: Double): String = when (time) {
            in 0.0..45.0 -> "畅通 (${String.format("%.2f", time)}s)"
            in 45.0..120.0 -> "缓慢 (${String.format("%.2f", time)}s)"
            in 120.0..300.0 -> "拥堵 (${String.format("%.2f", time)}s)"
            in 300.0..Double.MAX_VALUE -> "严重拥堵 (${String.format("%.2f", time)}s)"
            else -> "暂无数据"
        }

        viewModelScope.launch {
            val chuServerStat = CFQServer.statUploadTime(0).toDouble()
            val maiServerStat = CFQServer.statUploadTime(1).toDouble()
            _uiState.update { currentValue ->
                currentValue.copy(
                    chuServerStat = makeServerStatText(chuServerStat),
                    maiServerStat = makeServerStatText(maiServerStat)
                )
            }
            // Log.i("Updater", "Got server stats: ${chuServerStat}s ${maiServerStat}s")
        }
    }

    fun updateUploadStat() {
        fun makeChunithmUploadStatText(phase: Int): String = when (phase) {
            0 -> "认证中"
            1 -> "更新最好成绩"
            2 -> "更新最近记录"
            3 -> "更新玩家信息"
            4 -> "更新出勤记录"
            5 -> "更新收藏品信息"
            6 -> "更新Rating列表"
            else -> "未开始上传"
        }

        fun makeMaimaiUploadStatText(phase: Int): String = when (phase) {
            0 -> "认证中"
            1 -> "更新玩家信息"
            2 -> "更新出勤记录"
            3 -> "更新收藏品信息"
            4 -> "更新最好成绩"
            5 -> "更新最近记录"
            else -> "未开始上传"
        }

        viewModelScope.launch {
            val uploadStats = CFQServer.statCheckUpload(token)
            _uiState.update { currentValue ->
                currentValue.copy(
                    chuUploadStat = makeChunithmUploadStatText(uploadStats[0]),
                    maiUploadStat = makeMaimaiUploadStatText(uploadStats[1])
                )
            }
            // Log.i("Updater", "Got upload stats: ${uploadStats[0]} ${uploadStats[1]}")
        }
    }

    fun updateQuickUploadStat() {
        viewModelScope.launch {
            val chuStat = CFQServer.apiHasTokenCache(0, token)
            val maiStat = CFQServer.apiHasTokenCache(1, token)

            _uiState.update { currentValue ->
                currentValue.copy(
                    chuTokenCacheStat = if (chuStat) "可上传" else "无缓存",
                    maiTokenCacheStat = if (maiStat) "可上传" else "无缓存",
                    canPerformChuQuickUpload = chuStat,
                    canPerformMaiQuickUpload = maiStat
                )
            }
        }
    }

    fun prepareVPN(context: Context): Intent? {
        VpnService.prepare(context)?.also {
            // Fisrt time
            return it
        }

        return null
    }

    fun startVPN(context: Context) {
        Intent(context, ChafenqiProxy::class.java).also {
            ChafenqiProxy().start(context)
        }
    }

    fun stopVPN(context: Context) {
        Intent(context, ChafenqiProxy::class.java).also {
            ChafenqiProxy().stop(context)
        }
    }

    fun buildUri(mode: Int): String {
        return Uri.parse(PORTAL_ADDRESS)
            .buildUpon()
            .appendPath(if (mode == 0) "upload_chunithm" else "upload_maimai")
            .appendQueryParameter("jwt", token)
            .build()
            .toString()
    }

    fun openWeChat(context: Context, uriHandler: UriHandler, snackbarHostState: SnackbarHostState) {
        try {
            uriHandler.openUri("weixin://")
        } catch (e: Exception) {
            // No WeChat?
            Log.e("Updater", "Cannot jump to WeChat, error: $e")
            Firebase.crashlytics.recordException(e)
            viewModelScope.launch { snackbarHostState.showSnackbar("无法打开微信，请检查权限或是否已安装") }
        }
    }

    fun openWeChatScan(
        context: Context,
        uriHandler: UriHandler,
        snackbarHostState: SnackbarHostState
    ) {
        try {
            uriHandler.openUri("weixin://")
        } catch (e: Exception) {
            // No WeChat?
            Log.e("Updater", "Cannot jump to WeChat, error: $e")
            viewModelScope.launch { snackbarHostState.showSnackbar("无法打开微信，请检查权限或是否已安装") }
        }
    }

    suspend fun triggerQuickUpload(game: Int): Boolean {
        try {
            if (CFQServer.apiIsUploading(game, token)) {
                return false
            }

            CFQServer.apiTriggerQuickUpload(game, token)
            return true
        } catch (e: Exception) {
            Log.e("Updater", "Failed to trigger quick upload, error: $e")
            return false
        }
    }

    suspend fun checkFishTokenState(): Boolean {
        if (CFQUser.remoteOptions.fishToken.isEmpty()) return true
        return FishServer.checkTokenValidity(CFQUser.remoteOptions.fishToken)
    }

    // Returns remote setting
    suspend fun setFishForwardState(state: Boolean): Boolean {
        return try {
            val result =
                CFQServer.apiUploadUserOption(token, "forwarding_fish", if (state) "1" else "0")
            if (result) {
                CFQServer.apiFetchUserOption(token, "forwarding_fish") == "1"
            } else {
                Log.e("Updater", "Server error while setting forward fish.")
                !state
            }
        } catch (e: Exception) {
            Log.e("Updater", "Failed to set fish forward to $state")
            e.printStackTrace()
            !state
        }
    }

}