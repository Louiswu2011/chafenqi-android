package com.nltv.chafenqi.view.module

import android.Manifest
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nltv.chafenqi.SCREEN_PADDING

@Composable
fun AppUpdaterDialog(snackbarHostState: SnackbarHostState) {
    val model: AppUpdaterViewModel = viewModel()

    if (model.showConfirmDialog) {
        AppUpdaterInfoDialog(
            currentVersionCode = model.currentVersionCode,
            currentBuildNumber = model.currentBuildNumber,
            latestVersionCode = model.latestVersionCode,
            latestBuildNumber = model.latestBuildNumber
        )
    }

    if (model.showDownloadDialog) {
        AppUpdaterDownloadDialog(snackbarHostState) {
            model.showConfirmDialog = false
        }
    }
}

@Composable
fun AppUpdaterInfoDialog(
    currentVersionCode: String,
    currentBuildNumber: Int,
    latestVersionCode: String,
    latestBuildNumber: Int
) {
    val model: AppUpdaterViewModel = viewModel()
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = { model.showConfirmDialog = false },
        confirmButton = {
            Button(onClick = {
                val permission = context.checkSelfPermission(Manifest.permission.INSTALL_PACKAGES)
                Log.i("AppUpdater", "Permission status: $permission")
                model.showConfirmDialog = false
                model.showDownloadDialog = true
            }) {
                Text(text = "更新")
            }
        },
        dismissButton = {
            Button(onClick = { model.showConfirmDialog = false }) {
                Text(text = "忽略")
            }
        },
        icon = { Icon(imageVector = Icons.Default.Update, contentDescription = "发现新版本") },
        title = { Text(text = "发现新版本") },
        text = {
            Text(
                text = "当前版本为：${currentVersionCode} (${currentBuildNumber})" +
                        "\n最新版本为：${latestVersionCode} (${latestBuildNumber})" +
                        "\n是否更新？"
            )
        }
    )
}

@Composable
fun AppUpdaterDownloadDialog(snackbarHostState: SnackbarHostState, onDismissRequest: () -> Unit) {
    val model: AppUpdaterViewModel = viewModel()
    val context = LocalContext.current
    val progress by model.progress.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        model.startUpdate(context, snackbarHostState)
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier

                .background(
                    MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(15.dp)
                )
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(SCREEN_PADDING * 2)
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.padding(top = SCREEN_PADDING))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "下载中")
                    Text(text = "${String.format("%.2f", progress * 100)}%")
                }
            }
        }
    }
}