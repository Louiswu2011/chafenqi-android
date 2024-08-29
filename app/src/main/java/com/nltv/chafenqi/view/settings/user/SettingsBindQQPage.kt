package com.nltv.chafenqi.view.settings.user

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.michaelflisar.composepreferences.core.PreferenceDivider
import com.michaelflisar.composepreferences.core.PreferenceInfo
import com.michaelflisar.composepreferences.core.PreferenceScreen
import com.michaelflisar.composepreferences.core.PreferenceSectionHeader
import com.michaelflisar.composepreferences.core.classes.PreferenceSettingsDefaults
import com.michaelflisar.composepreferences.screen.button.PreferenceButton
import com.michaelflisar.composepreferences.screen.input.PreferenceInputText
import com.nltv.chafenqi.networking.CFQServer
import com.nltv.chafenqi.view.settings.SettingsPageViewModel
import com.nltv.chafenqi.view.settings.SettingsTopBar
import kotlinx.coroutines.launch

@Composable
fun SettingsBindQQPage(navController: NavController) {
    val model: SettingsPageViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    var userQQ by remember {
        mutableStateOf("")
    }

    Scaffold(
        topBar = {
            SettingsTopBar(titleText = "绑定QQ号", navController = navController)
        },
        containerColor = MaterialTheme.colorScheme.surface,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        PreferenceScreen(
            Modifier.padding(paddingValues),
            settings = PreferenceSettingsDefaults.settings(),
            scrollable = true
        ) {
            PreferenceInfo(
                title = { Text(text = "当前状态") },
                subtitle = { Text(text = if (model.bindQQ.isEmpty()) "未绑定" else "已绑定QQ: ${model.bindQQ}") },
                icon = { Icon(imageVector = Icons.Default.Info, contentDescription = "当前状态") }
            )
            PreferenceDivider()
            PreferenceSectionHeader(title = { Text(text = "绑定QQ号") })
            PreferenceInputText(
                value = userQQ,
                onValueChange = { userQQ = it },
                title = { Text("QQ号") },
                icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "QQ号") }
            )
            PreferenceButton(
                onClick = {
                    if (userQQ.isEmpty()) {
                        scope.launch { snackbarHostState.showSnackbar("请输入QQ号") }
                        return@PreferenceButton
                    }
                    if (userQQ.toLongOrNull() == null) {
                        scope.launch { snackbarHostState.showSnackbar("格式错误，请重新输入") }
                        return@PreferenceButton
                    }

                    scope.launch {
                        CFQServer.apiUploadUserOption(model.token, "bindQQ", userQQ)
                        model.user.remoteOptions.bindQQ = userQQ
                        scope.launch { snackbarHostState.showSnackbar("绑定成功！") }
                        userQQ = ""
                        navController.navigateUp()
                    }
                },
                title = { Text(text = "绑定") }
            )
            PreferenceButton(
                onClick = {
                    userQQ = ""
                },
                title = { Text(text = "清空", color = MaterialTheme.colorScheme.error) }
            )
            PreferenceDivider()
            PreferenceInfo(
                title = { Text(text = "绑定QQ号以授权第三方服务访问您的数据") },
                subtitle = { Text(text = "目前支持的服务: Chieri Bot") },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "通知",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )
        }
    }
}