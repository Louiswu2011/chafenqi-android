package com.nltv.chafenqi.view.updater

import android.content.Context
import android.content.Intent
import android.net.VpnService
import androidx.lifecycle.ViewModel
import com.nltv.chafenqi.updater.ChafenqiProxy

class UpdaterViewModel: ViewModel() {

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
}