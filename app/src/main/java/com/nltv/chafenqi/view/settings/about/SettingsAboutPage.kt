package com.nltv.chafenqi.view.settings.about

import android.Manifest
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.michaelflisar.composepreferences.core.PreferenceInfo
import com.michaelflisar.composepreferences.core.PreferenceScreen
import com.michaelflisar.composepreferences.core.classes.PreferenceSettingsDefaults
import com.michaelflisar.composepreferences.core.hierarchy.PreferenceRootScope
import com.michaelflisar.composepreferences.screen.button.PreferenceButton
import com.nltv.chafenqi.BuildConfig
import com.nltv.chafenqi.view.home.HomeNavItem
import com.nltv.chafenqi.view.module.AppUpdaterDialog
import com.nltv.chafenqi.view.module.AppUpdaterViewModel
import com.nltv.chafenqi.view.settings.SettingsPageViewModel
import com.nltv.chafenqi.view.settings.SettingsTopBar
import kotlinx.coroutines.launch

@Composable
fun SettingsAboutPage(navController: NavController) {
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    Scaffold(
        topBar = { SettingsTopBar(titleText = "关于", navController = navController) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        PreferenceScreen(
            modifier = Modifier.padding(it),
            settings = PreferenceSettingsDefaults.settings(),
            scrollable = true
        ) {
            SettingsAboutGroup(navController = navController, snackbarHostState = snackbarHostState)
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PreferenceRootScope.SettingsAboutGroup(
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {
    val model: SettingsPageViewModel = viewModel()
    val updaterModel: AppUpdaterViewModel = viewModel()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current
    val packageInstallPermissionState =
        rememberPermissionState(permission = Manifest.permission.REQUEST_INSTALL_PACKAGES)

    AppUpdaterDialog(snackbarHostState = snackbarHostState)

    PreferenceInfo(
        title = { Text(text = "版本") },
        subtitle = { Text(text = BuildConfig.VERSION_NAME) },
        icon = { Icon(imageVector = Icons.Default.Info, contentDescription = "版本") }
    )
    PreferenceButton(
        onClick = {
            scope.launch {
                if (!packageInstallPermissionState.status.isGranted) {
                    packageInstallPermissionState.launchPermissionRequest()
                }
                updaterModel.checkUpdates(snackbarHostState)
            }
        },
        title = { Text(text = "检查新版本") },
        icon = { Icon(imageVector = Icons.Default.Update, contentDescription = "检查新版本") }
    )
    PreferenceButton(
        onClick = {
            scope.launch {
                try {
                    val key = "Eov_F10167LaSygnlljvkT2pNQahaIB4"
                    uriHandler.openUri("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D$key")
                } catch (e: Exception) {
                    Log.e("Settings", "Failed to open url, error: $e")
                    snackbarHostState.showSnackbar("无法打开加群链接，请稍后重试")
                }
            }
        },
        title = { Text(text = "加入QQ群") },
        subtitle = { Text(text = "提供反馈或交流") },
        icon = { Icon(imageVector = Icons.AutoMirrored.Filled.Chat, contentDescription = "加入QQ群") }
    )
    PreferenceButton(
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
        title = { Text(text = "前往Github") },
        subtitle = { Text(text = "查看App代码") },
        icon = { Icon(imageVector = Icons.Default.Code, contentDescription = "前往Github") }
    )
    PreferenceButton(
        onClick = { navController.navigate(HomeNavItem.Home.route + "/settings/about/acknowledge") },
        title = { Text(text = "鸣谢") },
        subtitle = { Text(text = "制作人员和爱发电人员名单") },
        icon = { Icon(imageVector = Icons.Default.WorkspacePremium, contentDescription = "鸣谢") }
    )
}