package com.nltv.chafenqi

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.IBinder
import android.os.ParcelFileDescriptor
import androidx.annotation.RequiresApi

class ChafenqiProxy : VpnService() {
    private val mConfigureIntent: PendingIntent by lazy {
        var activityFlag = PendingIntent.FLAG_UPDATE_CURRENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            activityFlag += PendingIntent.FLAG_MUTABLE
        }
        PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), activityFlag)
    }

    private lateinit var vpnInterface: ParcelFileDescriptor


    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    private fun connect() {
        vpnInterface = createVpnInterface()

    }

    private fun createVpnInterface(): ParcelFileDescriptor {
        return Builder()
            .addAddress("10.0.0.2", 32)
            .addRoute("0.0.0.0", 0)
            .setSession("查分器NEW")
            .setBlocking(true)
            .setConfigureIntent(mConfigureIntent)
            .also {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    it.setMetered(false)
                }
            }
            .establish() ?: throw IllegalStateException("Cannot create VPN interface")
    }

    private fun updateForegroundNotification(message: Int) {
        val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.createNotificationChannel(
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_ID,
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )
        startForeground(
            1, Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentText(getString(message))
                .setContentIntent(mConfigureIntent)
                .build()
        )
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "ChafenqiVPN"
    }
}