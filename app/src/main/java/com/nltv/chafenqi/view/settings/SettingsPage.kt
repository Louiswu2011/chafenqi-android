package com.nltv.chafenqi.view.settings

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ScubaDiving
import androidx.compose.material.icons.filled.Token
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.michaelflisar.composepreferences.core.PreferenceDivider
import com.michaelflisar.composepreferences.core.PreferenceInfo
import com.michaelflisar.composepreferences.core.PreferenceScreen
import com.michaelflisar.composepreferences.core.PreferenceSectionHeader
import com.michaelflisar.composepreferences.core.classes.PreferenceSettingsDefaults
import com.michaelflisar.composepreferences.core.hierarchy.PreferenceRootScope
import com.michaelflisar.composepreferences.screen.bool.PreferenceBool
import com.michaelflisar.composepreferences.screen.button.PreferenceButton
import com.michaelflisar.composepreferences.screen.list.PreferenceList
import com.nltv.chafenqi.BuildConfig
import com.nltv.chafenqi.LocalUserState
import com.nltv.chafenqi.storage.SettingsStore
import com.nltv.chafenqi.storage.SettingsStore.Companion.settingsStore
import com.nltv.chafenqi.storage.`object`.CFQPersistentData
import com.nltv.chafenqi.view.home.HomeNavItem
import com.nltv.chafenqi.view.module.AppUpdaterDialog
import com.nltv.chafenqi.view.module.AppUpdaterViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun SettingsPage(navController: NavController) {
    val model: SettingsPageViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val userState = LocalUserState.current
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    LaunchedEffect(Unit) {
        model.getCoilDiskCacheSize(context)
    }

    if (model.showLogoutAlert) {
        LogoutAlertDialog(onDismissRequest = { model.showLogoutAlert = false }) {
            // Logout here
            scope.launch {
                model.showLogoutAlert = false
                if (model.clearCachedCredentials(context)) {
                    userState.logout()
                } else {
                    Log.e("Settings", "Failed to clear previous credentials, abort logging out.")
                }
            }

        }
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "设置") },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回上一级"
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        PreferenceScreen(
            settings = PreferenceSettingsDefaults.settings(),
            scrollable = true,
            modifier = Modifier.padding(paddingValues)
        ) {
            SettingsEntry(navController, snackbarHostState)
        }
    }
}

@Composable
fun PreferenceRootScope.SettingsEntry(
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {
    SettingsUserGroup(navController)
    PreferenceDivider()

    SettingsHomeGroup()
    PreferenceDivider()

    SettingsQSTileGroup()
    PreferenceDivider()

    SettingsAdvancedGroup(snackbarHostState)
    PreferenceDivider()

    SettingsAboutGroup(navController, snackbarHostState)
}

@Composable
fun PreferenceRootScope.SettingsUserGroup(navController: NavController) {
    val model: SettingsPageViewModel = viewModel()
    val uiState by model.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            model.updateUserPremiumTime()
        }
    }

    PreferenceSectionHeader(title = { Text(text = "用户") })
    PreferenceInfo(
        title = { Text(text = "当前用户") },
        subtitle = { Text(text = model.username) },
        icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "当前用户") }
    )
    PreferenceButton(
        onClick = { navController.navigate(HomeNavItem.Home.route + "/settings/redeem") },
        title = { Text(text = "兑换会员") },
        subtitle = {
            Text(text = uiState.membershipStatus)
        },
        icon = { Icon(imageVector = Icons.Default.CardGiftcard, contentDescription = "兑换会员") }
    )
    PreferenceButton(
        onClick = { navController.navigate(HomeNavItem.Home.route + "/settings/bind/fish") },
        title = { Text(text = "绑定水鱼网账号") },
        icon = {
            Icon(
                imageVector = Icons.Default.ScubaDiving,
                contentDescription = "绑定水鱼网账号"
            )
        }
    )
    PreferenceButton(
        onClick = {
            model.showLogoutAlert = true
        },
        title = { Text("登出", color = MaterialTheme.colorScheme.error) },
        icon = {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = "登出",
                tint = MaterialTheme.colorScheme.error
            )
        }
    )
}

@Composable
fun PreferenceRootScope.SettingsHomeGroup() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val store = SettingsStore(context)
    val homeDefaultGame by store.homeDefaultGame.collectAsStateWithLifecycle(initialValue = 1)
    val homeShowRefreshButton by store.homeShowRefreshButton.collectAsStateWithLifecycle(
        initialValue = false
    )

    PreferenceSectionHeader(title = { Text(text = "主页") })
    PreferenceList(
        value = homeDefaultGame,
        onValueChange = { newValue ->
            scope.launch { store.setHomeDefaultGame(newValue) }
        },
        items = listOf(0, 1),
        itemTextProvider = { index -> GAME_LIST[index] },
        title = { Text(text = "默认游戏") },
        subtitle = { Text(text = "设置登录后显示的游戏") },
        icon = {
            Icon(
                imageVector = Icons.Default.VideogameAsset,
                contentDescription = "默认游戏"
            )
        },
        style = PreferenceList.Style.Spinner
    )
    PreferenceBool(
        value = homeShowRefreshButton,
        onValueChange = { newValue ->
            scope.launch { store.setHomeShowRefreshButton(newValue) }
        },
        title = { Text(text = "显示刷新按钮") },
        subtitle = { Text(text = "无法下拉刷新时可以使用") }
    )
}

@Composable
fun PreferenceRootScope.SettingsQSTileGroup() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val store = SettingsStore(context)
    val qsInheritBaseSettings by store.qsInheritBaseSettings.collectAsStateWithLifecycle(initialValue = true)
    val qsCopyTargetGame by store.qsCopyTargetGame.collectAsStateWithLifecycle(initialValue = 1)
    val qsCopyToClipboard by store.qsCopyToClipboard.collectAsStateWithLifecycle(initialValue = false)
    val qsShouldForward by store.qsShouldForward.collectAsStateWithLifecycle(initialValue = false)
    val qsShouldAutoJump by store.qsShouldAutoJump.collectAsStateWithLifecycle(initialValue = false)

    PreferenceSectionHeader(title = { Text(text = "快捷设置") })
    PreferenceBool(
        value = qsCopyToClipboard,
        onValueChange = { scope.launch { store.setQsCopyToClipboard(it) } },
        title = { Text(text = "自动复制传分链接") }
    )
    if (qsCopyToClipboard) {
        PreferenceList(
            value = qsCopyTargetGame,
            onValueChange = { scope.launch { store.setQsCopyTargetGameKey(it) } },
            items = listOf(1, 0),
            itemTextProvider = { GAME_LIST[it] },
            title = { Text(text = "目标游戏") },
            subtitle = { Text(text = "选择需要上传的游戏") },
            style = PreferenceList.Style.Spinner
        )
    }
    PreferenceBool(
        value = qsInheritBaseSettings,
        onValueChange = { scope.launch { store.setQsInheritBaseSettings(it) } },
        title = { Text(text = "与传分设置保持一致") },
        subtitle = { Text(text = "关闭来自定义快捷设置行为") }
    )
    if (!qsInheritBaseSettings) {
        PreferenceBool(
            value = qsShouldAutoJump,
            onValueChange = { scope.launch { store.setQsShouldAutoJumpKey(it) } },
            title = { Text(text = "自动跳转至微信") }
        )
        PreferenceBool(
            value = qsShouldForward,
            onValueChange = { scope.launch { store.setQsShouldForwardKey(it) } },
            title = { Text(text = "同步至水鱼网") }
        )
    }
}

@Composable
fun PreferenceRootScope.SettingsAdvancedGroup(snackbarHostState: SnackbarHostState) {
    val model: SettingsPageViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val store = SettingsStore(context)
    val loginAutoUpdateSongList by store.loginAutoUpdateSongList.collectAsStateWithLifecycle(initialValue = true)
    var showJwtToken by remember { mutableStateOf(false) }

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

    PreferenceSectionHeader(title = {
        Text(
            text = "高级",
            color = MaterialTheme.colorScheme.error
        )
    })
    PreferenceBool(
        value = loginAutoUpdateSongList,
        onValueChange = { scope.launch { store.setLoginAutoUpdateSongList(it) } },
        title = { Text(text = "保持歌曲列表为最新") },
        subtitle = { Text(text = "将会在每次启动前检测并自动更新歌曲列表") },
        icon = { Icon(imageVector = Icons.Default.CloudSync, contentDescription = "保持歌曲列表为最新") }
    )
    PreferenceButton(
        onClick = { model.showReloadListAlert = true },
        title = { Text(text = "刷新歌曲列表") },
        icon = { Icon(imageVector = Icons.Default.List, contentDescription = "刷新歌曲列表") }
    )
    PreferenceButton(
        onClick = {
            if (model.diskCacheSize.isNotEmpty()) {
                model.showClearCacheAlert = true
            }
        },
        title = { Text(text = "清空图片缓存") },
        subtitle = {
            val sizeString = model.diskCacheSize
            if (sizeString.isNotEmpty()) {
                Text(text = "当前占用：$sizeString")
            } else {
                Text(text = "暂无缓存")
            }
        },
        icon = { Icon(imageVector = Icons.Default.Delete, contentDescription = "清除图片缓存") }
    )
    PreferenceButton(
        onClick = { showJwtToken = !showJwtToken },
        title = { Text(text = "Token") },
        subtitle = { Text(text = if (showJwtToken) model.token else "点击显示/隐藏") },
        icon = { Icon(imageVector = Icons.Default.Token, contentDescription = "Token") }
    )
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

    PreferenceSectionHeader(title = { Text(text = "关于") })
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
        icon = { Icon(imageVector = Icons.Default.Chat, contentDescription = "加入QQ群") }
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
        onClick = { navController.navigate(HomeNavItem.Home.route + "/settings/acknowledge") },
        title = { Text(text = "鸣谢") },
        subtitle = { Text(text = "制作人员和爱发电人员名单") },
        icon = { Icon(imageVector = Icons.Default.WorkspacePremium, contentDescription = "鸣谢") }
    )
}

@Composable
fun SettingsUserInfoCard() {
    Surface(
        Modifier.fillMaxWidth()
    ) {
        Card {

        }
    }
}