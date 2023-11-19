package com.nltv.chafenqi.view.settings

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.michaelflisar.composepreferences.core.classes.PreferenceStyle
import com.michaelflisar.composepreferences.core.classes.PreferenceStyleDefaults
import com.michaelflisar.composepreferences.screen.button.PreferenceButton
import com.michaelflisar.composepreferences.screen.input.PreferenceInputText
import com.nltv.chafenqi.networking.FishServer
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBindFishPage(navController: NavController) {
    val model: SettingsPageViewModel = viewModel()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var fishUsername by remember {
        mutableStateOf("")
    }
    var fishPassword by remember {
        mutableStateOf("")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "绑定水鱼网账号") },
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
            Modifier.padding(paddingValues),
            settings = PreferenceSettingsDefaults.settings(),
            scrollable = true
        ) {
            PreferenceInfo(
                title = { Text(text = "当前状态") },
                subtitle = { Text(text = if (model.user.fishToken.isEmpty()) "未绑定" else "已绑定") },
                icon = { Icon(imageVector = Icons.Default.Info, contentDescription = "当前状态") }
            )
            PreferenceDivider()
            PreferenceSectionHeader(title = { Text(text = "登录到水鱼网") })
            PreferenceInputText(
                value = fishUsername,
                onValueChange = { fishUsername = it },
                title = { Text("用户名") },
                icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "用户名") }
            )
            PreferenceInputText(
                value = fishPassword,
                onValueChange = { fishPassword = it },
                title = { Text("密码") },
                icon = { Icon(imageVector = Icons.Default.Key, contentDescription = "密码") }
            )
            PreferenceButton(
                onClick = {
                    if (fishUsername.isEmpty() || fishPassword.isEmpty()) {
                        Toast.makeText(context, "请输入用户名和密码", Toast.LENGTH_SHORT).show()
                        return@PreferenceButton
                    }

                    scope.launch {
                        val token = FishServer.getUserToken(fishUsername, fishPassword)
                        if (token.isEmpty()) {
                            Toast.makeText(context, "用户名或密码错误", Toast.LENGTH_SHORT).show()
                            return@launch
                        }

                        model.user.fishToken = token
                        Toast.makeText(context, "绑定成功！", Toast.LENGTH_SHORT).show()
                        fishUsername = ""
                        fishPassword = ""
                        navController.navigateUp()
                    }
                },
                title = { Text(text = "绑定") }
            )
            PreferenceButton(
                onClick = {
                    fishUsername = ""
                    fishPassword = ""
                },
                title = { Text(text = "清空", color = MaterialTheme.colorScheme.error) }
            )
            PreferenceDivider()
            PreferenceInfo(
                title = { Text(text = "查分器将严格保护您的数据安全") },
                subtitle = { Text(text = "您的用户名和密码将只用于和水鱼服务器进行认证，不会存储至云端") },
                icon = { Icon(imageVector = Icons.Default.Security, contentDescription = "保障", tint = MaterialTheme.colorScheme.primary) }
            )
        }
    }
}