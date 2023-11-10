package com.nltv.chafenqi.view.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.alorma.compose.settings.ui.SettingsGroup
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.nltv.chafenqi.LocalUserState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(navController: NavController) {
    val model: SettingsPageViewModel = viewModel()
    val userState = LocalUserState.current

    var showLogoutDialog by remember {
        mutableStateOf(false)
    }

    if (showLogoutDialog) {
        LogoutAlertDialog(onDismissRequest = { showLogoutDialog = false }) {
            // Logout here
            showLogoutDialog = false
            userState.logout()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "设置") },
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
        Column(
            Modifier.padding(paddingValues)
        ) {
            SettingsGroup(
                title = { Text(text = "账户") }
            ) {

                SettingsMenuLink(
                    title = { Text(text = "兑换会员") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.CardMembership,
                            contentDescription = "兑换会员按钮图标"
                        )
                    },
                ) {

                }
                SettingsMenuLink(
                    title = { Text(text = "登出") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "登出",
                            tint = MaterialTheme.colorScheme.error
                        )
                    },
                ) {
                    showLogoutDialog = true
                }
            }
            SettingsGroup(
                title = { Text(text = "关于") }
            ) {
                SettingsMenuLink(
                    title = { Text(text = "鸣谢") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Handshake,
                            contentDescription = "鸣谢"
                        )
                    },
                ) {

                }
                SettingsMenuLink(
                    title = { Text(text = "版本") },
                    enabled = false,
                    subtitle = { Text(text = "v0.1a") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "版本"
                        )
                    },
                ) {}
            }
        }
    }
}

@Composable
fun LogoutAlertDialog(onDismissRequest: () -> Unit, onConfirmation: () -> Unit) {
    AlertDialog(
        icon = {
            Icon(imageVector = Icons.Default.Logout, contentDescription = "登出")
        },
        title = {
            Text(text = "确定要登出吗？")
        },
        text = {
            Text(text = "将会回到登录界面。")
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirmation) {
                Text(text = "确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "取消")
            }
        }
    )
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