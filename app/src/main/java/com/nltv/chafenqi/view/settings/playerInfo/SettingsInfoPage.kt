package com.nltv.chafenqi.view.settings.playerInfo

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
import com.michaelflisar.composepreferences.screen.list.PreferenceList
import com.nltv.chafenqi.extension.LEVEL_STRINGS
import com.nltv.chafenqi.storage.SettingsStore
import com.nltv.chafenqi.view.settings.SettingsTopBar
import kotlinx.coroutines.launch

@Composable
fun SettingsInfoPage(navController: NavController) {
    Scaffold (
        topBar = { SettingsTopBar(titleText = "玩家信息", navController = navController) }
    ) {
        PreferenceScreen (
            modifier = Modifier.padding(it),
            settings = PreferenceSettingsDefaults.settings(),
            scrollable = true
        ) {
            SettingsInfoLevelsGroup()
        }
    }
}

@Composable
fun PreferenceRootScope.SettingsInfoLevelsGroup() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val store = SettingsStore(context)
    val chuDefaultLevelIndex by store.infoLevelsChunithmDefaultLevel.collectAsStateWithLifecycle(initialValue = 18)
    val maiDefaultLevelIndex by store.infoLevelsMaimaiDefaultLevel.collectAsStateWithLifecycle(initialValue = 18)
    
    PreferenceSectionHeader(title = { Text(text = "歌曲完成度") })
    PreferenceList(
        value = maiDefaultLevelIndex,
        onValueChange = { scope.launch { store.setInfoLevelsMaimaiDefaultLevel(it) } },
        items = (0..22).toList(),
        itemTextProvider = { LEVEL_STRINGS[it] },
        title = { Text(text = "舞萌DX默认等级") })
    PreferenceList(
        value = chuDefaultLevelIndex,
        onValueChange = { scope.launch { store.setInfoLevelsChunithmDefaultLevel(it) } },
        items = (0..22).toList(),
        itemTextProvider = { LEVEL_STRINGS[it] },
        title = { Text(text = "中二节奏默认等级") })
}