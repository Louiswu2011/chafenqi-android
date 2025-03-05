package com.nltv.chafenqi.updater

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.VpnService
import android.os.Binder
import android.os.IBinder
import android.os.Parcel
import android.os.ParcelFileDescriptor
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.os.RemoteException
import androidx.preference.PreferenceManager
import android.text.TextUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import com.nltv.chafenqi.ChafenqiApplication
import com.nltv.chafenqi.R
import java.io.IOException
import androidx.core.content.edit

class ChafenqiProxy : VpnService() {
    private val PREF_RUNNING = "pref_running"
    private val TAG = "ChafenqiProxy.Service"
    private val ACTION_START = "start"
    private val ACTION_STOP = "stop"

    @Volatile
    private var wlInstance: WakeLock? = null

    private var lastBuilder: VpnService.Builder? = null
    private var vpn: ParcelFileDescriptor? = null

    @Suppress("unused")
    @Synchronized
    private fun getLock(context: Context): WakeLock? {
        if (wlInstance == null) {
            val pm = context.getSystemService(POWER_SERVICE) as PowerManager
            wlInstance = pm.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                context.getString(R.string.app_name) + " wakelock"
            )
            wlInstance?.setReferenceCounted(true)
        }
        return wlInstance
    }

    init {
        System.loadLibrary("tun2http")
    }


    fun start(context: Context) {
        val intent = Intent(context, ChafenqiProxy::class.java)
        intent.action = ACTION_START
        context.startForegroundService(intent)
    }

    fun stop(context: Context) {
        val intent = Intent(context, ChafenqiProxy::class.java)
        intent.action = ACTION_STOP
        context.startForegroundService(intent)
    }

    private external fun jni_init()

    private external fun jni_start(
        tun: Int,
        fwd53: Boolean,
        rcode: Int,
        proxyIp: String?,
        proxyPort: Int
    )

    private external fun jni_stop(tun: Int)

    private external fun jni_get_mtu(): Int

    private external fun jni_done()

    override fun onBind(intent: Intent?): IBinder {
        return ServiceBinder()
    }

    private fun start() {
        if (vpn == null) {
            lastBuilder = getBuilder()
            vpn = startVPN(lastBuilder)
            checkNotNull(vpn) { getString(R.string.msg_start_failed) }
            startNative(vpn!!)
        }
    }

    private fun stop() {
        if (vpn != null) {
            stopNative(vpn!!)
            stopVPN(vpn!!)
            vpn = null
        }
        stopForeground(Service.STOP_FOREGROUND_REMOVE)
    }

    override fun onRevoke() {
        Log.i(TAG, "Revoke")
        stop()
        vpn = null
        super.onRevoke()
    }

    @Throws(SecurityException::class)
    private fun startVPN(builder: Builder?): ParcelFileDescriptor? {
        return try {
            builder!!.establish()
        } catch (ex: SecurityException) {
            throw ex
        } catch (ex: Throwable) {
            Log.e(
                TAG, """
     $ex
     ${Log.getStackTraceString(ex)}
     """.trimIndent()
            )
            null
        }
    }

    private fun getBuilder(): Builder {
        // Build VPN service
        val builder = Builder()
        builder.setSession(getString(R.string.app_name))

        // VPN address
        builder.addAddress("10.1.10.1", 32)
        builder.addAddress("fd00:1:fd00:1:fd00:1:fd00:1", 128)
        builder.addRoute("0.0.0.0", 0)
        builder.addRoute("0:0:0:0:0:0:0:0", 0)
        val dnsList: MutableList<String> =
            Util.getDefaultDNS(ChafenqiApplication.applicationContext()).toMutableList()
        for (dns in dnsList) {
            Log.i(TAG, "default DNS:$dns")
            builder.addDnsServer(dns)
        }

        // MTU
        val mtu = jni_get_mtu()
        Log.i(TAG, "MTU=$mtu")
        builder.setMtu(mtu)

        return builder
    }

    private fun startNative(vpn: ParcelFileDescriptor) {
        val proxyHost = "43.139.107.206"
        val proxyPort = 8999
        if (!TextUtils.isEmpty(proxyHost)) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            jni_start(vpn.fd, false, 3, proxyHost, proxyPort)
            prefs.edit { putBoolean(PREF_RUNNING, true) }
        }
    }

    private fun stopNative(vpn: ParcelFileDescriptor) {
        try {
            jni_stop(vpn.fd)
        } catch (ex: Throwable) {
            // File descriptor might be closed
            Log.e(
                TAG, """
     $ex
     ${Log.getStackTraceString(ex)}
     """.trimIndent()
            )
            jni_stop(-1)
        }
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.edit { putBoolean(PREF_RUNNING, false) }
    }

    private fun stopVPN(pfd: ParcelFileDescriptor) {
        Log.i(TAG, "Stopping")
        try {
            pfd.close()
        } catch (ex: IOException) {
            Log.e(
                TAG, """
     $ex
     ${Log.getStackTraceString(ex)}
     """.trimIndent()
            )
        }
    }

    // Called from native code
    @Suppress("unused")
    private fun nativeExit(reason: String?) {
        Log.w(TAG, "Native exit reason=$reason")
        if (reason != null) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            prefs.edit { putBoolean("enabled", false) }
        }
    }

    // Called from native code
    @Suppress("unused")
    private fun nativeError(error: Int, message: String) {
        Log.w(TAG, "Native error $error: $message")
    }

    @Suppress("unused")
    private fun isSupported(protocol: Int): Boolean {
        return protocol == 1 || protocol == 59 || protocol == 6 || protocol == 17 /* UDP */
    }

    override fun onCreate() {
        // Native init
        jni_init()
        super.onCreate()
        val channelId = createNotificationChannel(this)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.drawable.app_icon)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setContentTitle("查分器代理")
            .setContentText("代理正在运行中")
            .build()
        startForeground(101, notification)
    }

    private fun createNotificationChannel(
        context: Context,
    ): String {
        val chan = NotificationChannel(
            "chafenqi-proxy",
            "vpn-service", NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return "chafenqi-proxy"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "Received $intent")
        if (intent == null) {
            return START_STICKY
        }
        if (ACTION_START == intent.action) {
            start()
        }
        if (ACTION_STOP == intent.action) {
            stop()
            stopSelf()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        Log.i(TAG, "Destroy")
        try {
            if (vpn != null) {
                stopNative(vpn!!)
                stopVPN(vpn!!)
                vpn = null
            }
        } catch (ex: Throwable) {
            Log.e(
                TAG, """
     $ex
     ${Log.getStackTraceString(ex)}
     """.trimIndent()
            )
        }
        jni_done()
        super.onDestroy()
    }

    class ServiceBinder : Binder() {
        @Throws(RemoteException::class)
        public override fun onTransact(
            code: Int,
            data: Parcel,
            reply: Parcel?,
            flags: Int
        ): Boolean {
            // see Implementation of android.net.VpnService.Callback.onTransact()
            if (code == LAST_CALL_TRANSACTION) {
                ChafenqiProxy().onRevoke()
                return true
            }
            return super.onTransact(code, data, reply, flags)
        }

    }
}