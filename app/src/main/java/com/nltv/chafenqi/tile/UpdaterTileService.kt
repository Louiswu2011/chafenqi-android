package com.nltv.chafenqi.tile

import android.app.PendingIntent
import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.net.VpnService
import android.os.PersistableBundle
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import com.nltv.chafenqi.storage.SettingsStore
import com.nltv.chafenqi.storage.user.CFQUser
import com.nltv.chafenqi.updater.ChafenqiProxy
import com.nltv.chafenqi.view.updater.PORTAL_ADDRESS
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class UpdaterTileService : TileService() {
    private var isVpnRunning = false

    override fun onStartListening() {
        super.onStartListening()
        qsTile.label = "传分代理"
        qsTile.contentDescription = qsTile.label
        qsTile.state = if (isVpnRunning) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        qsTile.updateTile()
    }

    override fun onClick() {
        super.onClick()
        qsTile.state =
            if (qsTile.state == Tile.STATE_INACTIVE) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        isVpnRunning = if (qsTile.state == Tile.STATE_ACTIVE) {
            startVPN()
            doExtraActions()
            true
        } else {
            stopVPN()
            false
        }
        qsTile.updateTile()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun doExtraActions() {
        GlobalScope.launch {
            val store = SettingsStore(applicationContext)
            val token = CFQUser.token
            val clipboardManager =
                applicationContext.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

            val qsInheritBaseSettings = store.qsInheritBaseSettings.firstOrNull()
            val qsCopyTargetGame = store.qsCopyTargetGame.firstOrNull()
            val qsCopyToClipboard = store.qsCopyToClipboard.firstOrNull() ?: 1
            val qsShouldForward = store.qsShouldForward.firstOrNull()
            val qsShouldAutoJump = store.qsShouldAutoJump.firstOrNull()

            val updaterShouldForward = store.uploadShouldForward.firstOrNull()
            val updaterShouldAutoJump = store.uploadShouldAutoJump.firstOrNull()

            val autoJump: Boolean
            val syncToFish: Int

            if (qsInheritBaseSettings != false) {
                autoJump = qsShouldAutoJump ?: false
                syncToFish = if (qsShouldForward == true) 1 else 0
            } else {
                autoJump = updaterShouldAutoJump ?: false
                syncToFish = if (updaterShouldForward == true) 1 else 0
            }

            // Log.i("TileService", "$token, $qsInheritBaseSettings, $qsCopyTargetGame, $qsCopyToClipboard, $qsShouldForward, $qsShouldAutoJump, $updaterShouldForward, $updaterShouldAutoJump")
            // Log.i("TileService", "$autoJump, $syncToFish")

            if (qsCopyToClipboard == true) {
                val link = Uri.parse(PORTAL_ADDRESS)
                    .buildUpon()
                    .appendPath(if (qsCopyTargetGame == 0) "upload_chunithm" else "upload_maimai")
                    .appendQueryParameter("jwt", token)
                    .appendQueryParameter("forwarding", syncToFish.toString())
                    .build()
                    .toString()
                val clipData = ClipData.newPlainText(link, link).apply {
                    description.extras = PersistableBundle().apply {
                        putBoolean(ClipDescription.MIMETYPE_TEXT_PLAIN, true)
                    }
                }
                clipboardManager.setPrimaryClip(clipData)
                // Log.i("TileService", link)
            }

            if (autoJump) {
                try {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.data = Uri.parse("weixin://")
                    if (android.os.Build.VERSION.SDK_INT >= 34) {
                        val pendingIntent = PendingIntent.getBroadcast(
                            applicationContext,
                            0,
                            intent,
                            PendingIntent.FLAG_IMMUTABLE
                        )
                        startActivityAndCollapse(pendingIntent)
                    } else {
                        startActivity(intent)
                    }
                } catch (e: Exception) {
                    Log.e("TileService", "Cannot open weixin, error: $e")
                }
            }
        }
    }

    private fun startVPN() {
        val intent = VpnService.prepare(applicationContext)
        intent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent?.also { startActivity(it) }.run {
            Intent(applicationContext, ChafenqiProxy::class.java).also {
                ChafenqiProxy().start(applicationContext)
            }
        }
    }

    private fun stopVPN() {
        Intent(applicationContext, ChafenqiProxy::class.java).also {
            ChafenqiProxy().stop(applicationContext)
        }
    }
}