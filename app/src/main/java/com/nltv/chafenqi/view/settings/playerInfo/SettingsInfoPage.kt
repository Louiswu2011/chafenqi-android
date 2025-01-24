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
import com.nltv.chafenqi.extension.CHUNITHM_LEVEL_STRINGS
import com.nltv.chafenqi.extension.MAIMAI_LEVEL_STRINGS
import com.nltv.chafenqi.storage.SettingsStore
import com.nltv.chafenqi.view.settings.SettingsTopBar
import kotlinx.coroutines.launch

@Composable
fun SettingsInfoPage(navController: NavController) {
    Scaffold(
        topBar = { SettingsTopBar(titleText = "玩家信息", navController = navController) }
    ) {

    }
}

//@Composable
//fun PreferenceRootScope.SettingsInfoLevelsGroup() {
//    val context = LocalContext.current
//    val scope = rememberCoroutineScope()
//    val store = SettingsStore(context)
//    val chuDefaultLevelIndex by store.infoLevelsChunithmDefaultLevel.collectAsStateWithLifecycle(
//        initialValue = 18
//    )
//    val maiDefaultLevelIndex by store.infoLevelsMaimaiDefaultLevel.collectAsStateWithLifecycle(
//        initialValue = 18
//    )
//
//    // Log.i("Settings", "Default level indexes: $chuDefaultLevelIndex $maiDefaultLevelIndex")
//    PreferenceSectionHeader(title = { Text(text = "歌曲完成度") })
//    PreferenceList(
//        value = maiDefaultLevelIndex,
//        onValueChange = { newValue -> scope.launch { store.setInfoLevelsMaimaiDefaultLevel(newValue) } },
//        items = (0..22).toList(),
//        itemTextProvider = { MAIMAI_LEVEL_STRINGS[it] },
//        title = { Text(text = "舞萌DX默认等级") },
//        style = PreferenceList.Style.Spinner
//    )
//    PreferenceList(
//        value = chuDefaultLevelIndex,
//        onValueChange = { newValue ->
//            scope.launch {
//                store.setInfoLevelsChunithmDefaultLevel(
//                    newValue
//                )
//            }
//        },
//        items = (0..22).toList(),
//        itemTextProvider = { CHUNITHM_LEVEL_STRINGS[it] },
//        title = { Text(text = "中二节奏默认等级") },
//        style = PreferenceList.Style.Spinner
//    )
//}