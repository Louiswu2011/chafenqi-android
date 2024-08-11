package com.nltv.chafenqi.view.settings.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.michaelflisar.composepreferences.screen.input.PreferenceInputNumber
import com.michaelflisar.composepreferences.screen.list.PreferenceList
import com.nltv.chafenqi.storage.SettingsStore
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
            SettingsHomeGroup()
        }
    }
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
    val logDefaultPricePerRound by store.logDefaultPricePerRound.collectAsStateWithLifecycle(initialValue = 3f)

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
    PreferenceInputNumber(
        value = logDefaultPricePerRound,
        onValueChange = {
            scope.launch { store.setLogDefaultPricePerRound(it) }
        },
        title = { Text(text = "默认单局价格") },
        subtitle = { Text(text = "设置出勤记录中估算的单局价格") }
    )
}