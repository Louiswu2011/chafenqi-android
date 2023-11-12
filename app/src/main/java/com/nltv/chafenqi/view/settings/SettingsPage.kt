package com.nltv.chafenqi.view.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ScubaDiving
import androidx.compose.material.icons.filled.Token
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.michaelflisar.composepreferences.core.PreferenceDivider
import com.michaelflisar.composepreferences.core.PreferenceInfo
import com.michaelflisar.composepreferences.core.PreferenceScreen
import com.michaelflisar.composepreferences.core.PreferenceSectionHeader
import com.michaelflisar.composepreferences.core.classes.PreferenceSettingsDefaults
import com.michaelflisar.composepreferences.core.hierarchy.PreferenceRootScope
import com.michaelflisar.composepreferences.screen.button.PreferenceButton
import com.nltv.chafenqi.BuildConfig
import com.nltv.chafenqi.LocalUserState
import com.nltv.chafenqi.storage.`object`.CFQPersistentData
import com.nltv.chafenqi.view.home.HomeNavItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(navController: NavController) {
    val model: SettingsPageViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val userState = LocalUserState.current

    if (model.showLogoutAlert) {
        LogoutAlertDialog(onDismissRequest = { model.showLogoutAlert = false }) {
            // Logout here
            model.showLogoutAlert = false
            userState.logout()
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
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
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
        }
    ) { paddingValues ->
        PreferenceScreen (
            settings = PreferenceSettingsDefaults.settings(),
            scrollable = true,
            modifier = Modifier.padding(paddingValues)
        ) {
            SettingsEntry(navController)
        }
    }
}

@Composable
fun PreferenceRootScope.SettingsEntry(navController: NavController) {
    SettingsUserGroup(navController)
    PreferenceDivider()
    SettingsAdvancedGroup()
    PreferenceDivider()
    SettingsAboutGroup(navController)
}

@Composable
fun PreferenceRootScope.SettingsUserGroup(navController: NavController) {
    val model: SettingsPageViewModel = viewModel()

    PreferenceSectionHeader(title = { Text(text = "用户") })
    PreferenceInfo(
        title = { Text(text = "当前用户") },
        subtitle = { Text(text = model.username) },
        icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "当前用户") }
    )
    PreferenceButton(
        onClick = { navController.navigate(HomeNavItem.Home.route + "/settings/redeem") },
        title = { Text(text = "兑换会员") },
        icon = { Icon(imageVector = Icons.Default.CardGiftcard, contentDescription = "兑换会员") }
    )
    PreferenceButton(
        onClick = { navController.navigate(HomeNavItem.Home.route + "/settings/bind/fish") },
        title = { Text(text = "绑定水鱼网账号") },
        icon = { Icon(imageVector = Icons.Default.ScubaDiving, contentDescription = "绑定水鱼网账号") }
    )
    PreferenceButton(
        onClick = {
            model.showLogoutAlert = true
        },
        title = { Text("登出") },
        icon = { Icon(imageVector = Icons.Default.Logout, contentDescription = "登出") }
    )
}

@Composable
fun PreferenceRootScope.SettingsAdvancedGroup() {
    val model: SettingsPageViewModel = viewModel()
    var showJwtToken by remember { mutableStateOf(false) }

    PreferenceSectionHeader(title = { Text(text = "高级", color = MaterialTheme.colorScheme.error) })
    PreferenceButton(
        onClick = { model.showReloadListAlert = true },
        title = { Text(text = "刷新歌曲列表") },
        icon = { Icon(imageVector = Icons.Default.List, contentDescription = "刷新歌曲列表") }
    )
    PreferenceButton(
        onClick = { showJwtToken = !showJwtToken },
        title = { Text(text = "Token") },
        subtitle = { Text(text = if (showJwtToken) model.token else "点击显示/隐藏") },
        icon = { Icon(imageVector = Icons.Default.Token, contentDescription = "Token") }
    )
}

@Composable
fun PreferenceRootScope.SettingsAboutGroup(navController: NavController) {
    PreferenceSectionHeader(title = { Text(text = "关于") })
    PreferenceInfo(
        title = { Text(text = "版本") },
        subtitle = { Text(text = BuildConfig.VERSION_NAME) },
        icon = { Icon(imageVector = Icons.Default.Info, contentDescription = "版本") }
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