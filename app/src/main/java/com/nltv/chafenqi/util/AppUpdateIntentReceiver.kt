package com.nltv.chafenqi.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build.VERSION.SDK_INT
import android.os.Parcelable
import android.util.Log

class AppUpdateIntentReceiver : BroadcastReceiver() {
    private val tag = "AppInstaller"

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(tag, "Received.")
        when (val status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, -1)) {
            PackageInstaller.STATUS_PENDING_USER_ACTION -> {
                val activityIntent =
                    intent.parcelable<Intent>(Intent.EXTRA_INTENT)

                if (activityIntent != null) {
                    context.startActivity(activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                }
            }

            PackageInstaller.STATUS_SUCCESS -> {}

            else -> {
                val msg = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE)

                Log.e(tag, "received $status and $msg")
            }
        }
    }
}

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}