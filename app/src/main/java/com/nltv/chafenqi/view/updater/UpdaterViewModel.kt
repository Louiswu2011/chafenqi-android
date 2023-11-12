package com.nltv.chafenqi.view.updater

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.net.VpnService
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.UriHandler
import androidx.lifecycle.ViewModel
import com.nltv.chafenqi.storage.CFQUser
import com.nltv.chafenqi.updater.ChafenqiProxy

val PORTAL_ADDRESS = "http://43.139.107.206:8083/"

class UpdaterViewModel : ViewModel() {
    private val token = CFQUser.token

    var shouldShowQRCode by mutableStateOf(false)

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