package com.nltv.chafenqi.view.settings.qs

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.michaelflisar.composepreferences.core.PreferenceDivider
import com.michaelflisar.composepreferences.core.PreferenceInfo
import com.michaelflisar.composepreferences.core.PreferenceScreen
import com.michaelflisar.composepreferences.core.classes.PreferenceSettingsDefaults
import com.michaelflisar.composepreferences.core.hierarchy.PreferenceRootScope
import com.michaelflisar.composepreferences.screen.bool.PreferenceBool
import com.michaelflisar.composepreferences.screen.button.PreferenceButton
import com.michaelflisar.composepreferences.screen.list.PreferenceList
import com.nltv.chafenqi.storage.SettingsStore
import com.nltv.chafenqi.view.settings.GAME_LIST
import com.nltv.chafenqi.view.settings.SettingsPageViewModel
import com.nltv.chafenqi.view.settings.SettingsTopBar
import kotlinx.coroutines.launch
import me.zhanghai.compose.preference.LocalPreferenceFlow
import me.zhanghai.compose.preference.footerPreference
import me.zhanghai.compose.preference.preference
import me.zhanghai.compose.preference.switchPreference

@Composable
fun SettingsQSTilePage(navController: NavController) {
    val settings by LocalPreferenceFlow.current.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val model: SettingsPageViewModel = viewModel()

    Scaffold(
        topBar = { SettingsTopBar(titleText = "快捷设置", navController = navController) }
    ) {
        LazyColumn (
            modifier = Modifier.padding(it)
                .fillMaxSize()
        ) {
            switchPreference(
                key = "qsCopyToClipboard",
                defaultValue = false,
                title = { Text(text = "自动复制传分链接") },
                summary = { Text(text = "在控制中心打开查分代理后自动复制传分链接到剪贴板") },
            )
            switchPreference(
                key = "qsInheritBaseSettings",
                defaultValue = false,
                title = { Text(text = "与传分设置保持一致") },
                summary = { Text(text = "关闭来自定义快捷设置行为") },
            )
            settings.get<Boolean>("qsInheritBaseSettings")?.let {
                if (!it) {
                    switchPreference(
                        key = "qsShouldAutoJump",
                        defaultValue = false,
                        title = { Text(text = "自动跳转到查分代理") },
                        summary = { Text(text = "在控制中心打开查分代理后自动跳转到查分代理") },
                    )
                }
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
                preference(
                    key = "qsAddToControlCenter",
                    title = { Text(text = "添加到控制中心...") },
                    onClick = { model.requestAddTile(context) }
                )
            } else {
                footerPreference(
                    key = "qsManualAddPrompt",
                    summary = { Text(text = "若要使用快捷设置，请在控制中心中添加“传分代理”") },
                )
            }
        }
    }
}