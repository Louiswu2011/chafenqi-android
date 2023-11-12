package com.nltv.chafenqi.view.updater

import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.alorma.compose.settings.ui.SettingsSwitch
import dev.burnoo.compose.rememberpreference.rememberBooleanPreference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdaterHomePage(navController: NavController) {
    val model: UpdaterViewModel = viewModel()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "传分") },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Box {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                ProxyToggle()
                UpdaterWechatActions()
                Divider()
                UpdaterClipboardActions(navController)
                Divider()
            }
            if (model.shouldShowQRCode) {
                UpdaterQRCodePage()
            }
        }
    }
}

@Composable
fun ProxyToggle() {
    var isVpnOn by remember {
        mutableStateOf(false)
    }

    val model: UpdaterViewModel = viewModel()
    val context = LocalContext.current

    val vpnLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            if (it.resultCode == Activity.RESULT_OK) {
                model.startVPN(context)
                isVpnOn = true
            }
        })

    SettingsSwitch(
        title = { Text(text = "开关") },
        // icon = { Icon(imageVector = Icons.Default.Wifi, contentDescription = "代理开关") },
        onCheckedChange = { checked ->
            isVpnOn = checked
            if (checked) {
                val intent = model.prepareVPN(context)
                intent?.also { vpnLauncher.launch(it) } ?: run {
                    model.startVPN(context)
                }
            } else {
                model.stopVPN(context)
            }
        }
    )
}

@Composable
fun UpdaterClipboardActions(navController: NavController) {
    val model: UpdaterViewModel = viewModel()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    var shouldForward by rememberBooleanPreference(keyName = "shouldForward")

    fun makeToast() {
        Toast.makeText(context, "已复制到剪贴板", Toast.LENGTH_SHORT).show()
    }

    SettingsSwitch(
        title = { Text(text = "同步到水鱼网") },
        subtitle = { Text(text = "需要在设置中绑定账号") },
        onCheckedChange = {
            shouldForward = it
        }
    )
    SettingsMenuLink(
        title = { Text(text = "复制舞萌DX链接") },
        icon = { Icon(imageVector = Icons.Default.Link, contentDescription = "复制舞萌DX链接") }
    ) {
        clipboardManager.setText(AnnotatedString(model.buildUri(1, shouldForward ?: false)))
        makeToast()
    }
    SettingsMenuLink(
        title = { Text(text = "复制中二节奏链接") },
        icon = { Icon(imageVector = Icons.Default.Link, contentDescription = "复制中二节奏链接") }
    ) {
        clipboardManager.setText(AnnotatedString(model.buildUri(0, shouldForward ?: false)))
        makeToast()
    }
    SettingsMenuLink(
        title = { Text(text = "生成二维码") },
        icon = { Icon(imageVector = Icons.Default.QrCode, contentDescription = "复制中二节奏链接") }
    ) {
        model.shouldShowQRCode = true
    }
}

@Composable
fun UpdaterWechatActions() {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val model: UpdaterViewModel = viewModel()

    SettingsMenuLink(
        title = { Text(text = "跳转到微信") },
    ) {
        model.openWeChat(context, uriHandler)
    }
}
