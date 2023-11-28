package com.nltv.chafenqi.view.settings.qs

import androidx.compose.foundation.layout.padding
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
import com.michaelflisar.composepreferences.core.PreferenceSectionHeader
import com.michaelflisar.composepreferences.core.classes.PreferenceSettingsDefaults
import com.michaelflisar.composepreferences.core.hierarchy.PreferenceRootScope
import com.michaelflisar.composepreferences.screen.bool.PreferenceBool
import com.michaelflisar.composepreferences.screen.list.PreferenceList
import com.nltv.chafenqi.storage.SettingsStore
import com.nltv.chafenqi.view.settings.GAME_LIST
import com.nltv.chafenqi.view.settings.SettingsTopBar
import com.nltv.chafenqi.view.settings.home.SettingsHomeGroup
import kotlinx.coroutines.launch

@Composable
fun SettingsQSTilePage(navController: NavController) {
    Scaffold (
        topBar = { SettingsTopBar(titleText = "快捷设置", navController = navController) }
    ) {
        PreferenceScreen (
            modifier = Modifier.padding(it),
            settings = PreferenceSettingsDefaults.settings(),
            scrollable = true
        ) {
            SettingsQSTileGroup()
        }
    }
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