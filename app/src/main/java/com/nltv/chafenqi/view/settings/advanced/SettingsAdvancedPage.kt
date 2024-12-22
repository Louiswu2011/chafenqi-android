package com.nltv.chafenqi.view.settings.advanced

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Token
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.michaelflisar.composepreferences.core.PreferenceScreen
import com.michaelflisar.composepreferences.core.classes.PreferenceSettingsDefaults
import com.michaelflisar.composepreferences.core.hierarchy.PreferenceRootScope
import com.michaelflisar.composepreferences.screen.bool.PreferenceBool
import com.michaelflisar.composepreferences.screen.button.PreferenceButton
import com.nltv.chafenqi.storage.SettingsStore
import com.nltv.chafenqi.storage.persistent.CFQPersistentData
import com.nltv.chafenqi.view.settings.ClearCacheAlertDialog
import com.nltv.chafenqi.view.settings.ReloadSongListAlertDialog
import com.nltv.chafenqi.view.settings.ReloadSongListDialog
import com.nltv.chafenqi.view.settings.SettingsPageViewModel
import com.nltv.chafenqi.view.settings.SettingsTopBar
import kotlinx.coroutines.launch
import me.zhanghai.compose.preference.preference
import me.zhanghai.compose.preference.switchPreference

@Composable
fun SettingsAdvancedPage(navController: NavController) {
    val model: SettingsPageViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showJwtToken by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        model.getCoilDiskCacheSize(context)
        model.updateSongListVersion()
    }

    if (model.showReloadListAlert) {
        ReloadSongListAlertDialog(onDismissRequest = { model.showReloadListAlert = false }) {
            model.showReloadListAlert = false
            model.isReloadingList = true
            scope.launch {
                CFQPersistentData.clearData(context)
                CFQPersistentData.loadData(context = context)
                model.isReloadingList = false
            }
        }
    }

    if (model.isReloadingList) {
        ReloadSongListDialog {
            model.isReloadingList = false
        }
    }

    val snackbarHostState = remember {
        SnackbarHostState()
    }
    Scaffold(
        topBar = { SettingsTopBar(titleText = "高级", navController = navController) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        LazyColumn (
            modifier = Modifier.padding(it)
                .fillMaxSize()
        ) {
            switchPreference(
                key = "loginAutoUpdateSongList",
                defaultValue = true,
                title = { Text(text = "保持歌曲列表为最新") },
                summary = { Text(text = "将会在每次启动时检测并自动更新歌曲列表") },
            )

            preference(
                key = "refreshSongList",
                title = { Text(text = "刷新歌曲列表") },
                summary = {
                    Column {
                        Text(text = "舞萌DX更新日期：${model.maiSongListVersionString}")
                        Text(text = "中二节奏更新日期：${model.chuSongListVersionString}")
                    }
                },
                onClick = {
                    model.showReloadListAlert = true
                }
            )

            preference(
                key = "clearCache",
                title = { Text(text = "清除缓存") },
                summary = {
                    val sizeString = model.diskCacheSize
                    if (sizeString.isNotEmpty()) {
                        Text(text = "当前占用：$sizeString")
                    } else {
                        Text(text = "暂无缓存")
                    }
                },
                onClick = {
                    model.showClearCacheAlert = true
                }
            )

            preference(
                key = "showJwtToken",
                title = { Text(text = "Token") },
                summary = { Text(text = if (showJwtToken) model.token else "点击显示/隐藏") },
                onClick = { showJwtToken = !showJwtToken }
            )
        }
    }

    if (model.showClearCacheAlert) {
        ClearCacheAlertDialog(onDismissRequest = { model.showClearCacheAlert = false }) {
            try {
                model.clearCoilCache(context)
                model.showClearCacheAlert = false
            } catch (e: Exception) {
                Log.e("SettingsPage", "Cannot clear coil cache, error: $e")
                scope.launch { snackbarHostState.showSnackbar("无法清除缓存，请稍后重试") }
            }
        }
    }
}