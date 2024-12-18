package com.nltv.chafenqi.view.settings.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.michaelflisar.composepreferences.core.PreferenceScreen
import com.michaelflisar.composepreferences.core.classes.PreferenceSettingsDefaults
import com.michaelflisar.composepreferences.core.hierarchy.PreferenceRootScope
import com.michaelflisar.composepreferences.screen.bool.PreferenceBool
import com.michaelflisar.composepreferences.screen.button.PreferenceButton
import com.michaelflisar.composepreferences.screen.input.PreferenceInputNumber
import com.michaelflisar.composepreferences.screen.list.PreferenceList
import com.nltv.chafenqi.storage.SettingsStore
import com.nltv.chafenqi.view.home.HomeNavItem
import com.nltv.chafenqi.view.settings.GAME_LIST
import com.nltv.chafenqi.view.settings.SettingsTopBar
import kotlinx.coroutines.launch

@Composable
fun SettingsHomePage(navController: NavController) {
    Scaffold(
        topBar = { SettingsTopBar(titleText = "主页", navController = navController) }
    ) {
        PreferenceScreen(
            modifier = Modifier.padding(it),
            settings = PreferenceSettingsDefaults.settings(),
            scrollable = true
        ) {
            SettingsHomeGroup(navController)
        }
    }
}

@Composable
fun PreferenceRootScope.SettingsHomeGroup(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val store = SettingsStore(context)
    val homeDefaultGame by store.homeDefaultGame.collectAsStateWithLifecycle(initialValue = 1)
    val homeShowRefreshButton by store.homeShowRefreshButton.collectAsStateWithLifecycle(
        initialValue = false
    )
    val logDefaultPricePerRound by store.logDefaultPricePerRound.collectAsStateWithLifecycle(
        initialValue = 3f
    )
    val homeUseThemedColor by store.homeUseThemedColor.collectAsStateWithLifecycle(
        initialValue = true
    )
    val homeShowTeamButton by store.homeShowTeamButton.collectAsStateWithLifecycle(
        initialValue = true
    )

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
    PreferenceBool(
        value = homeUseThemedColor,
        onValueChange = { newValue ->
            scope.launch { store.setHomeUseThemedColor(newValue) }
        },
        title = { Text(text = "使用主题色") },
        subtitle = { Text(text = "使用当前游戏版本的主题色作为用户信息卡片的背景") }
    )
    PreferenceBool(
        value = homeShowTeamButton,
        onValueChange = { newValue ->
            scope.launch { store.setHomeShowTeamButton(newValue) }
        },
        title = { Text(text = "显示团队按钮") },
        subtitle = { Text(text = "在用户信息卡下方显示跳转到团队页面的按钮") }
    )
    PreferenceInputNumber(
        value = logDefaultPricePerRound,
        onValueChange = {
            scope.launch { store.setLogDefaultPricePerRound(it) }
        },
        title = { Text(text = "默认单局价格") },
        subtitle = { Text(text = "设置出勤记录中估算的单局价格") }
    )
    PreferenceButton(
        onClick = { navController.navigate(HomeNavItem.Home.route + "/settings/home/arrangement") },
        title = { Text(text = "主页排序") },
        subtitle = { Text(text = "设置主页各模块的显示顺序") }
    )
}