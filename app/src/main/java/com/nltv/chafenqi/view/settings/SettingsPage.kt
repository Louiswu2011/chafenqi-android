package com.nltv.chafenqi.view.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.nltv.chafenqi.view.home.HomeNavItem
import me.zhanghai.compose.preference.preference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopBar(titleText: String, navController: NavController, action: @Composable () -> Unit = {}) {
    LargeTopAppBar(
        title = { Text(text = titleText) },
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回上一级"
                )
            }
        },
        actions = { action() }
    )
}

@Composable
fun SettingsPage(navController: NavController) {
    Scaffold(
        topBar = { SettingsTopBar(titleText = "设置", navController = navController) },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        LazyColumn (
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            preference(
                key = "user_preferences",
                onClick = { navController.navigate(HomeNavItem.Home.route + "/settings/user") },
                title = { Text(text = "用户") },
                summary = { Text(text = "绑定账号、兑换会员、登出") },
                icon = { Icon(imageVector = Icons.Default.Contacts, contentDescription = "用户") }
            )
            preference(
                key = "home_preferences",
                onClick = { navController.navigate(HomeNavItem.Home.route + "/settings/home") },
                title = { Text(text = "主页") },
                summary = { Text(text = "默认游戏、刷新按钮、单局币价、主页排序") },
                icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "主页") }
            )
            // TODO: Fix default level index not working
            /*PreferenceButton(
                onClick = { navController.navigate(HomeNavItem.Home.route + "/settings/playerInfo") },
                title = { Text(text = "玩家信息") },
                subtitle = { Text(text = "歌曲完成度") },
                icon = { Icon(imageVector = Icons.Default.Info, contentDescription = "玩家信息") }
            )*/
            preference(
                key = "qs_preferences",
                onClick = { navController.navigate(HomeNavItem.Home.route + "/settings/qsTile") },
                title = { Text(text = "快捷设置") },
                summary = { Text(text = "自动复制链接、跳转至微信") },
                icon = { Icon(imageVector = Icons.Default.Widgets, contentDescription = "快捷设置") }
            )
            preference(
                key = "advanced_preferences",
                onClick = { navController.navigate(HomeNavItem.Home.route + "/settings/advanced") },
                title = { Text(text = "高级") },
                summary = { Text(text = "图片缓存、歌曲列表、Token") },
                icon = { Icon(imageVector = Icons.Default.DeveloperMode, contentDescription = "高级") }
            )
            preference(
                key = "about_preferences",
                onClick = { navController.navigate(HomeNavItem.Home.route + "/settings/about") },
                title = { Text(text = "关于") },
                summary = { Text(text = "版本、鸣谢、QQ群、Github") },
                icon = { Icon(imageVector = Icons.Default.Info, contentDescription = "关于") }
            )
        }
    }
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