package com.nltv.chafenqi.view.settings.about

import android.Manifest
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.nltv.chafenqi.BuildConfig
import com.nltv.chafenqi.view.home.HomeNavItem
import com.nltv.chafenqi.view.module.AppUpdaterDialog
import com.nltv.chafenqi.view.module.AppUpdaterViewModel
import com.nltv.chafenqi.view.settings.SettingsTopBar
import kotlinx.coroutines.launch
import me.zhanghai.compose.preference.preference

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SettingsAboutPage(navController: NavController) {
    val uriHandler = LocalUriHandler.current
    val updaterModel: AppUpdaterViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val packageInstallPermissionState =
        rememberPermissionState(permission = Manifest.permission.REQUEST_INSTALL_PACKAGES)

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    Scaffold(
        topBar = { SettingsTopBar(titleText = "关于", navController = navController) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        LazyColumn (
            modifier = Modifier.padding(it)
                .fillMaxSize()
        ) {
            preference(
                key = "appVersion",
                title = { Text(text = "版本") },
                summary = { Text(text = BuildConfig.VERSION_NAME) }
            )

            preference(
                key = "checkNewVersion",
                title = { Text(text = "检查新版本") },
                onClick = {
                    scope.launch {
                        if (!packageInstallPermissionState.status.isGranted) {
                            packageInstallPermissionState.launchPermissionRequest()
                        }
                        updaterModel.checkUpdates(snackbarHostState)
                    }
                }
            )

            preference(
                key = "joinQQGroup",
                title = { Text(text = "加入QQ群") },
                onClick = {
                    scope.launch {
                        try {
                            val key = "8dHBqzp08fLPsbl9Wxdxi6W0vCMnzX8b"
                            uriHandler.openUri("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D$key")
                        } catch (e: Exception) {
                            Log.e("Settings", "Failed to open url, error: $e")
                            snackbarHostState.showSnackbar("无法打开加群链接，请稍后重试")
                        }
                    }
                },
                summary = { Text(text = "提供反馈或交流") }
            )

            preference(
                key = "openGithub",
                title = { Text(text = "前往Github") },
                onClick = {
                    scope.launch {
                        try {
                            uriHandler.openUri("https://github.com/louiswu2011/chafenqi-android")
                        } catch (e: Exception) {
                            Log.e("Settings", "Failed to open url, error: $e")
                            snackbarHostState.showSnackbar("无法打开Github，请稍后重试")
                        }
                    }
                },
                summary = { Text(text = "查看App源代码") }
            )

            preference(
                key = "openAcknowledgments",
                title = { Text(text = "鸣谢") },
                onClick = {
                    navController.navigate(HomeNavItem.Home.route + "/settings/about/acknowledge")
                },
                summary = { Text(text = "制作人员和爱发电人员名单") }
            )
        }

        AppUpdaterDialog(snackbarHostState = snackbarHostState)
    }
}