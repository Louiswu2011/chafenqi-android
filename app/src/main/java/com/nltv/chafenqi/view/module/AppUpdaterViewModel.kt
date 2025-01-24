package com.nltv.chafenqi.view.module

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nltv.chafenqi.BuildConfig
import com.nltv.chafenqi.networking.CFQServer
import io.ktor.client.call.body
import io.ktor.client.request.prepareGet
import io.ktor.client.request.url
import io.ktor.http.contentLength
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readRemaining
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.io.readByteArray
import java.io.File
import java.nio.file.Files

class AppUpdaterViewModel : ViewModel() {
    private var _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress.asStateFlow()
    var showConfirmDialog by mutableStateOf(false)
    var showDownloadDialog by mutableStateOf(false)

    var latestVersionCode = ""
    var latestBuildNumber = 0
    private val currentVersionString = BuildConfig.VERSION_NAME
    val currentVersionCode = currentVersionString.split(" ")[0]
    val currentBuildNumber = currentVersionString.split(" ")[1]
        .removePrefix("(")
        .removeSuffix(")")
        .toInt()

    fun startUpdate(context: Context, snackbarHostState: SnackbarHostState) {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                download(context, snackbarHostState)
            }
        }
    }

    private suspend fun download(context: Context, snackbarHostState: SnackbarHostState) {
        showDownloadDialog = true
        val apkFile = File(context.cacheDir.resolve("latest.apk").toURI())
        withContext(Dispatchers.IO) {
            Files.deleteIfExists(apkFile.toPath())
            try {
                val request = CFQServer.client.prepareGet {
                    url("${CFQServer.defaultPath}/download/android/latest")
                }
                request.execute { response ->
                    var offset = 0f
                    val channel: ByteReadChannel = response.body()
                    val contentLength = response.contentLength()?.toInt() ?: 0
                    while (!channel.isClosedForRead) {
                        val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                        while (!packet.exhausted()) {
                            val bytes = packet.readByteArray()
                            apkFile.appendBytes(bytes)
                            _progress.update { _ ->
                                offset / contentLength
                            }
                            offset += bytes.size
                            Log.i(
                                "AppUpdater",
                                "Downloading latest apk ${offset / contentLength * 100}% ($offset/$contentLength)"
                            )
                        }
                    }
                    Log.i("AppUpdater", "Finished download latest apk, start installing...")
                    install(context, apkFile)
                    showDownloadDialog = false
                }
            } catch (e: Exception) {
                Log.e("AppUpdater", "Failed to download apk, error: $e")
                snackbarHostState.showSnackbar("下载更新时出错，请稍后再试")
                showDownloadDialog = false
            }
        }
    }

    private fun install(context: Context, file: File) {
        val apkUri = FileProvider.getUriForFile(context, "com.nltv.chafenqi.provider", file)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(
            apkUri,
            "application/vnd.android.package-archive"
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(intent)
    }

    fun checkUpdates(snackbarHostState: SnackbarHostState, silent: Boolean = false) {
        viewModelScope.launch {
            val versionData = CFQServer.apiFetchLatestVersion()
            Log.i(
                "AppUpdater",
                "Current version: $currentVersionCode (${currentBuildNumber}), latest version: ${versionData.androidVersionCode} (${versionData.androidBuild})"
            )
            if (!versionData.isLatest(currentVersionCode, currentBuildNumber)) {
                latestVersionCode = versionData.androidVersionCode
                latestBuildNumber = versionData.androidBuild.toIntOrNull() ?: 0
                showConfirmDialog = true
            } else {
                if (!silent) {
                    snackbarHostState.showSnackbar("当前已是最新版本")
                }
            }
        }
    }
}