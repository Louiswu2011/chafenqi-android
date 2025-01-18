package com.nltv.chafenqi.view.settings.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.navigation.NavController
import com.nltv.chafenqi.view.home.HomeNavItem
import com.nltv.chafenqi.view.settings.GAME_LIST
import com.nltv.chafenqi.view.settings.SettingsTopBar
import me.zhanghai.compose.preference.ListPreferenceType
import me.zhanghai.compose.preference.listPreference
import me.zhanghai.compose.preference.preference
import me.zhanghai.compose.preference.switchPreference
import me.zhanghai.compose.preference.textFieldPreference

@Composable
fun SettingsHomePage(navController: NavController) {
    Scaffold(
        topBar = { SettingsTopBar(titleText = "主页", navController = navController) }
    ) {
        LazyColumn(
            modifier = Modifier.padding(it)
                .fillMaxSize()
        ) {
            listPreference(
                key = "homeDefaultGame",
                defaultValue = 1,
                values = listOf(0, 1),
                valueToText = { buildAnnotatedString { append(GAME_LIST[it]) } },
                title = { Text(text = "默认游戏") },
                summary = { Text(text = "设置登录后显示的游戏") },
                type = ListPreferenceType.DROPDOWN_MENU
            )
            switchPreference(
                key = "homeShowRefreshButton",
                defaultValue = false,
                title = { Text(text = "显示刷新按钮") },
                summary = { Text(text = "无法下拉刷新时可以使用") },
            )
            switchPreference(
                key = "homeUseThemedColor",
                defaultValue = true,
                title = { Text(text = "使用主题色") },
                summary = { Text(text = "使用当前游戏版本的主题色作为用户信息卡片的背景") },
            )
            switchPreference(
                key = "homeShowTeamButton",
                defaultValue = true,
                title = { Text(text = "显示团队按钮") },
                summary = { Text(text = "在用户信息卡下方显示跳转到团队页面的按钮") },
            )
            textFieldPreference(
                key = "logDefaultPricePerRound",
                defaultValue = 3f,
                title = { Text(text = "默认单局价格") },
                summary = { Text(text = "设置出勤记录中估算的单局价格") },
                textToValue = { it.toFloatOrNull() ?: 3f },
            )
            preference(
                key = "homeArrangement",
                title = { Text(text = "主页排序") },
                summary = { Text(text = "设置主页各模块的显示顺序") },
                onClick = {
                    navController.navigate(HomeNavItem.Home.route + "/settings/home/arrangement")
                }
            )
        }
    }
}