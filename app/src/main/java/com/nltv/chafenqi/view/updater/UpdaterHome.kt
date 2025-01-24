package com.nltv.chafenqi.view.updater

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.michaelflisar.composepreferences.core.PreferenceDivider
import com.michaelflisar.composepreferences.core.PreferenceInfo
import com.michaelflisar.composepreferences.core.PreferenceScreen
import com.michaelflisar.composepreferences.core.PreferenceSectionHeader
import com.michaelflisar.composepreferences.core.classes.Dependency
import com.michaelflisar.composepreferences.core.classes.PreferenceSettingsDefaults
import com.michaelflisar.composepreferences.core.hierarchy.PreferenceRootScope
import com.michaelflisar.composepreferences.screen.bool.PreferenceBool
import com.michaelflisar.composepreferences.screen.button.PreferenceButton
import com.michaelflisar.composepreferences.screen.list.PreferenceList
import com.nltv.chafenqi.networking.CFQServer
import com.nltv.chafenqi.storage.SettingsStore
import com.nltv.chafenqi.view.home.HomeNavItem
import com.nltv.chafenqi.view.settings.GAME_LIST
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdaterHomePage(navController: NavController) {
    val model: UpdaterViewModel = viewModel()
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    LaunchedEffect(Unit) {
        model.startRefreshTask()
        if (!model.checkFishTokenState()) {
            when (snackbarHostState.showSnackbar(
                message = "水鱼网Token已过期，请重新进行登录。",
                actionLabel = "前往",
                withDismissAction = true,
                duration = SnackbarDuration.Indefinite
            )) {
                SnackbarResult.ActionPerformed -> {
                    navController.navigate(HomeNavItem.Home.route + "/settings/user/bind/fish")
                }

                else -> {}
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            model.stopRefreshTask()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "传分") },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                actions = {
                    IconButton(onClick = { navController.navigate(HomeNavItem.Uploader.route + "/help") }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Help,
                            contentDescription = "传分帮助"
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box {
            PreferenceScreen(
                settings = PreferenceSettingsDefaults.settings(),
                scrollable = true,
                modifier = Modifier.padding(paddingValues)
            ) {
                UpdaterProxyGroup(snackbarHostState)
                PreferenceDivider()

                UpdaterQuickActionsGroup(snackbarHostState)
                PreferenceDivider()

                UpdaterClipboardGroup(snackbarHostState)
                PreferenceDivider()

                UpdaterSettingsGroup(model, snackbarHostState)
                // PreferenceDivider()
            }
            if (model.shouldShowQRCode) {
                UpdaterQRCodePage(snackbarHostState)
            }
        }
    }
}

@Composable
fun PreferenceRootScope.UpdaterProxyGroup(snackbarHostState: SnackbarHostState) {
    val model: UpdaterViewModel = viewModel()
    val uiState by model.uiState.collectAsState()

    PreferenceSectionHeader(title = { Text(text = "代理") })
    ProxyToggle()
    PreferenceInfo(
        title = { Text(text = "传分状态") },
        subtitle = { Text(text = "舞萌DX: ${uiState.maiUploadStat}\n" + "中二节奏: ${uiState.chuUploadStat}") },
        // icon = { Icon(imageVector = Icons.Default.Info, contentDescription = "传分状态") }
    )
    PreferenceInfo(
        title = { Text(text = "服务器状态") },
        subtitle = { Text(text = "舞萌DX: ${uiState.maiServerStat}\n" + "中二节奏: ${uiState.chuServerStat}") },
        // icon = { Icon(imageVector = Icons.Default.AccessTime, contentDescription = "服务器状态") }
    )
    UpdaterWechatActions(snackbarHostState)
}

@Composable
fun PreferenceRootScope.ProxyToggle() {
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
    PreferenceBool(
        value = isVpnOn,
        onValueChange = { value ->
            isVpnOn = value
            if (isVpnOn) {
                val intent = model.prepareVPN(context)
                intent?.also { vpnLauncher.launch(it) } ?: run {
                    model.startVPN(context)
                }
            } else {
                model.stopVPN(context)
            }
        },
        title = { Text(text = "开关") }
    )
}

@Composable
fun PreferenceRootScope.UpdaterClipboardGroup(snackbarHostState: SnackbarHostState) {
    val model: UpdaterViewModel = viewModel()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val store = SettingsStore(context)

    fun makeToast() {
        scope.launch { snackbarHostState.showSnackbar("已复制到剪贴板") }
    }

    PreferenceSectionHeader(title = { Text(text = "链接和二维码") })

    PreferenceButton(
        onClick = {
            clipboardManager.setText(AnnotatedString(model.buildUri(1)))
            makeToast()
        },
        title = { Text(text = "复制舞萌DX链接") },
        icon = { Icon(imageVector = Icons.Default.Link, contentDescription = "复制舞萌DX链接") }
    )
    PreferenceButton(
        onClick = {
            clipboardManager.setText(AnnotatedString(model.buildUri(0)))
            makeToast()
        },
        title = { Text(text = "复制中二节奏链接") },
        icon = { Icon(imageVector = Icons.Default.Link, contentDescription = "复制中二节奏链接") }
    )
    PreferenceButton(
        onClick = { model.shouldShowQRCode = true },
        title = { Text(text = "生成二维码") },
        icon = { Icon(imageVector = Icons.Default.QrCode, contentDescription = "生成二维码") }
    )
}

@Composable
fun PreferenceRootScope.UpdaterWechatActions(snackbarHostState: SnackbarHostState) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val model: UpdaterViewModel = viewModel()

    PreferenceButton(
        onClick = { model.openWeChat(context, uriHandler, snackbarHostState) },
        title = { Text(text = "跳转到微信") },
        icon = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = "跳转到微信"
            )
        }
    )
}

@Composable
fun PreferenceRootScope.UpdaterQuickActionsGroup(snackbarHostState: SnackbarHostState) {
    val model: UpdaterViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val store = SettingsStore(context)
    val uiState by model.uiState.collectAsStateWithLifecycle()
    var uploadGame by rememberSaveable { mutableIntStateOf(1) }


    PreferenceSectionHeader(title = { Text(text = "快速上传") })
    PreferenceList(
        value = uploadGame,
        onValueChange = { uploadGame = it },
        items = listOf(1, 0),
        itemTextProvider = { GAME_LIST[it] },
        title = { Text(text = "游戏") },
        style = PreferenceList.Style.Spinner
    )
    PreferenceInfo(
        title = { Text(text = "状态") },
        subtitle = {
            Text(
                text = when (uploadGame) {
                    0 -> uiState.chuTokenCacheStat
                    1 -> uiState.maiTokenCacheStat
                    else -> ""
                }
            )
        }
    )
    PreferenceButton(
        onClick = {
            scope.launch {
                if (model.triggerQuickUpload(uploadGame)) {
                    snackbarHostState.showSnackbar("提交成功，请留意传分状态")
                } else {
                    snackbarHostState.showSnackbar("已有上传任务，请勿重复提交")
                }
            }
        },
        title = { Text(text = "提交上传请求") },
        icon = { Icon(imageVector = Icons.Default.Upload, contentDescription = "上传") },
        enabled = when (uploadGame) {
            0 -> if (uiState.canPerformChuQuickUpload) Dependency.Enabled else Dependency.Disabled
            1 -> if (uiState.canPerformMaiQuickUpload) Dependency.Enabled else Dependency.Disabled
            else -> Dependency.Disabled
        }
    )
}

@Composable
fun PreferenceRootScope.UpdaterSettingsGroup(
    model: UpdaterViewModel,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val store = SettingsStore(context)
    var shouldForward by rememberSaveable {
        mutableStateOf(false)
    }
    var isUploading by rememberSaveable {
        mutableStateOf(false)
    }
    val shouldAutoJump by store.uploadShouldAutoJump.collectAsStateWithLifecycle(initialValue = false)

    LaunchedEffect(Unit) {
        isUploading = true
        shouldForward = CFQServer.apiFetchUserOption(model.token, "forwarding_fish", "boolean").toBoolean()
        isUploading = false
    }

    PreferenceSectionHeader(title = { Text(text = "设置") })
    PreferenceBool(
        value = shouldForward,
        onValueChange = { newValue ->
            scope.launch {
                Log.i("UpdaterHome", "Setting fish forward to $newValue")
                isUploading = true
                val remoteSetting = model.setFishForwardState(newValue)
                if (remoteSetting == newValue) {
                    shouldForward = newValue
                    isUploading = false
                    Log.i("UpdaterHome", "Success, remote setting: $remoteSetting")
                } else {
                    isUploading = false
                    Log.e("UpdaterHome", "Failed, remote setting: $remoteSetting")
                    snackbarHostState.showSnackbar("更改设置失败，请稍后再试")
                }
            }
        },
        title = { Text(text = "同步到水鱼网") },
        subtitle = { Text(text = "需要在设置中绑定账号") },
        enabled = if (isUploading) Dependency.Disabled else Dependency.Enabled
    )
    PreferenceBool(
        value = shouldAutoJump,
        onValueChange = {
            scope.launch {
                store.setUploadShouldAutoJump(it)
            }
        },
        title = { Text(text = "自动跳转") },
        subtitle = { Text(text = "开启代理后自动跳转至微信扫一扫") }
    )
}
