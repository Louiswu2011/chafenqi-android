package com.nltv.chafenqi.view.updater

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.net.VpnService
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.UriHandler
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nltv.chafenqi.networking.CFQServer
import com.nltv.chafenqi.storage.CFQUser
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
    val chuUploadStat: String = "暂未上传"
)

class UpdaterViewModel : ViewModel() {
    private val token = CFQUser.token
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
            _uiState.update {  currentValue ->
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

    fun buildUri(mode: Int, shouldForward: Boolean): String {
        return Uri.parse(PORTAL_ADDRESS)
            .buildUpon()
            .appendPath(if (mode == 0) "upload_chunithm" else "upload_maimai")
            .appendQueryParameter("jwt", token)
            .appendQueryParameter("forwarding", if (shouldForward) "1" else "0")
            .build()
            .toString()
    }

    fun openWeChat(context: Context, uriHandler: UriHandler) {
        try {
            val mananger = context.packageManager
            mananger.getPackageInfo("", PackageManager.GET_ACTIVITIES)
            uriHandler.openUri("weixin://")
        } catch (e: Exception) {
            // No WeChat?
            Toast.makeText(context, "无法打开微信，请检查权限或是否已安装", Toast.LENGTH_LONG).show()
        }
    }

    fun openWeChatScan(context: Context, uriHandler: UriHandler) {
        try {
            val mananger = context.packageManager
            mananger.getPackageInfo("", PackageManager.GET_ACTIVITIES)
            uriHandler.openUri("weixin://scanqrcode")
        } catch (e: Exception) {
            // No WeChat?
            Toast.makeText(context, "无法打开微信，请检查权限或是否已安装", Toast.LENGTH_LONG).show()
        }
    }
}