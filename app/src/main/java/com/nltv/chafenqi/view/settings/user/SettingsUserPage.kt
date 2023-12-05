package com.nltv.chafenqi.view.settings.user

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ScubaDiving
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.michaelflisar.composepreferences.core.PreferenceInfo
import com.michaelflisar.composepreferences.core.PreferenceScreen
import com.michaelflisar.composepreferences.core.PreferenceSectionHeader
import com.michaelflisar.composepreferences.core.classes.PreferenceSettingsDefaults
import com.michaelflisar.composepreferences.core.hierarchy.PreferenceRootScope
import com.michaelflisar.composepreferences.screen.button.PreferenceButton
import com.nltv.chafenqi.view.home.HomeNavItem
import com.nltv.chafenqi.view.settings.SettingsPageViewModel
import com.nltv.chafenqi.view.settings.SettingsTopBar
import kotlinx.coroutines.launch

@Composable
fun SettingsUserPage(navController: NavController) {
    Scaffold (
        topBar = { SettingsTopBar(titleText = "用户", navController = navController) }
    ) {
        PreferenceScreen (
            modifier = Modifier.padding(it),
            settings = PreferenceSettingsDefaults.settings(),
            scrollable = true
        ) {
            SettingsUserGroup(navController)
        }
    }
}

@Composable
fun PreferenceRootScope.SettingsUserGroup(navController: NavController) {
    val model: SettingsPageViewModel = viewModel()
    val uiState by model.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            model.updateUserPremiumTime()
        }
    }

    PreferenceInfo(
        title = { Text(text = "当前用户") },
        subtitle = { Text(text = model.username) },
        icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "当前用户") }
    )
    PreferenceButton(
        onClick = { navController.navigate(HomeNavItem.Home.route + "/settings/user/redeem") },
        title = { Text(text = "兑换会员") },
        subtitle = {
            Text(text = uiState.membershipStatus)
        },
        icon = { Icon(imageVector = Icons.Default.CardGiftcard, contentDescription = "兑换会员") }
    )
    PreferenceButton(
        onClick = { navController.navigate(HomeNavItem.Home.route + "/settings/user/bind/fish") },
        title = { Text(text = "绑定水鱼网账号") },
        icon = {
            Icon(
                imageVector = Icons.Default.ScubaDiving,
                contentDescription = "绑定水鱼网账号"
            )
        }
    )
    PreferenceButton(
        onClick = { navController.navigate(HomeNavItem.Home.route + "/settings/user/bind/qq") },
        title = { Text(text = "绑定QQ号") },
        icon = {
            Icon(
                imageVector = Icons.Default.ZoomIn,
                contentDescription = "绑定QQ号"
            )
        }
    )
    PreferenceButton(
        onClick = {
            model.showLogoutAlert = true
        },
        title = { Text("登出", color = MaterialTheme.colorScheme.error) },
        icon = {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = "登出",
                tint = MaterialTheme.colorScheme.error
            )
        }
    )
}