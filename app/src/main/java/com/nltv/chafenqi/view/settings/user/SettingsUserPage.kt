package com.nltv.chafenqi.view.settings.user

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ScubaDiving
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maxkeppeker.sheets.core.models.base.Header
import com.maxkeppeker.sheets.core.models.base.IconSource
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.info.InfoDialog
import com.maxkeppeler.sheets.info.models.InfoBody
import com.maxkeppeler.sheets.info.models.InfoSelection
import com.maxkeppeler.sheets.input.InputDialog
import com.maxkeppeler.sheets.input.models.InputHeader
import com.maxkeppeler.sheets.input.models.InputSelection
import com.maxkeppeler.sheets.input.models.InputTextField
import com.maxkeppeler.sheets.input.models.InputTextFieldType
import com.maxkeppeler.sheets.input.models.ValidationResult
import com.michaelflisar.composepreferences.core.PreferenceInfo
import com.michaelflisar.composepreferences.core.PreferenceScreen
import com.michaelflisar.composepreferences.core.classes.PreferenceSettingsDefaults
import com.michaelflisar.composepreferences.core.hierarchy.PreferenceRootScope
import com.michaelflisar.composepreferences.screen.button.PreferenceButton
import com.nltv.chafenqi.LocalUserState
import com.nltv.chafenqi.networking.CFQServer
import com.nltv.chafenqi.networking.FishServer
import com.nltv.chafenqi.view.home.HomeNavItem
import com.nltv.chafenqi.view.settings.LogoutAlertDialog
import com.nltv.chafenqi.view.settings.SettingsPageViewModel
import com.nltv.chafenqi.view.settings.SettingsTopBar
import kotlinx.coroutines.launch
import me.zhanghai.compose.preference.preference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsUserPage(navController: NavController) {
    val model: SettingsPageViewModel = viewModel()
    val uiState by model.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val userState = LocalUserState.current
    val context = LocalContext.current
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val bindFishUseState = rememberUseCaseState()
    val bindFishOption = listOf(
        InputTextField(
            header = InputHeader(
                title = "请输入水鱼用户名",
                icon = IconSource(Icons.Default.Person)
            ),
            validationListener = { value ->
                if (value?.isNotBlank() == true) ValidationResult.Valid else ValidationResult.Invalid("用户名不能为空")
            },
            key = "username"
        ),
        InputTextField(
            header = InputHeader(
                title = "请输入水鱼密码",
                icon = IconSource(Icons.Default.Key)
            ),
            visualTransformation = PasswordVisualTransformation(),
            validationListener = { value ->
                if (value?.isNotBlank() == true) ValidationResult.Valid else ValidationResult.Invalid("密码不能为空")
            },
            key = "password"
        )
    )

    val unbindFishUseState = rememberUseCaseState()
    val unbindFishBody = InfoBody.Default(
        bodyText = "解绑水鱼账号后，将无法同步数据到水鱼网。是否要解绑？"
    )

    val bindQQUseState = rememberUseCaseState()
    val bindQQOption = listOf(
        InputTextField(
            header = InputHeader(
                title = "请输入QQ号",
                icon = IconSource(Icons.Default.Person)
            ),
            validationListener = { value ->
                if (value?.isNotBlank() == true) {
                    if (value.toIntOrNull() != null) {
                        ValidationResult.Valid
                    } else {
                        ValidationResult.Invalid("QQ号格式不正确")
                    }
                } else {
                    ValidationResult.Invalid("QQ号不能为空")
                }
            },
            key = "qq"
        )
    )

    val unbindQQUseState = rememberUseCaseState()
    val unbindQQBody = InfoBody.Default(
        bodyText = "解绑QQ账号后，将无法使用Bot查询游玩信息。是否要解绑？"
    )

    val redeemUseState = rememberUseCaseState()
    val redeemOption = listOf(
        InputTextField(
            header = InputHeader(
                title = "请输入兑换码",
                icon = IconSource(Icons.Default.CardGiftcard)
            ),
            validationListener = { value ->
                if (value?.isNotBlank() == true) ValidationResult.Valid else ValidationResult.Invalid("兑换码不能为空")
            },
            key = "coupon"
        )
    )

    if (model.showLogoutAlert) {
        LogoutAlertDialog(onDismissRequest = { model.showLogoutAlert = false }) {
            // Logout here
            scope.launch {
                model.showLogoutAlert = false
                if (model.clearCachedCredentials(context)) {
                    userState.logout()
                } else {
                    snackbarHostState.showSnackbar("登出时发生错误，请重试")
                    Log.e("Settings", "Failed to clear previous credentials, abort logging out.")
                }
            }

        }
    }

    LaunchedEffect(Unit) {
        scope.launch {
            model.updateUserPremiumTime()
        }
    }

    Scaffold(
        topBar = { SettingsTopBar(titleText = "用户", navController = navController) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        LazyColumn (
            modifier = Modifier.padding(it)
                .fillMaxSize()
        ) {
            preference(
                key = "current_user",
                title = { Text(text = "当前用户") },
                summary = { Text(text = model.username) },
                icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "当前用户") }
            )
            preference(
                key = "redeem_membership",
                onClick = { navController.navigate(HomeNavItem.Home.route + "/settings/user/redeem") },
                title = { Text(text = "兑换会员") },
                summary = {
                    Text(text = uiState.membershipStatus)
                },
                icon = { Icon(imageVector = Icons.Default.CardGiftcard, contentDescription = "兑换会员") }
            )
            preference(
                key = "bind_fish_account",
                onClick = {
                    if (model.fishTokenState.isEmpty()) {
                        bindFishUseState.show()
                    } else {
                        unbindFishUseState.show()
                    }
                },
                title = { Text(text = "水鱼网账号") },
                summary = { Text(text = "${if(model.fishTokenState.isEmpty()) "未" else "已"}绑定") },
                icon = {
                    Icon(
                        imageVector = Icons.Default.ScubaDiving,
                        contentDescription = "绑定水鱼网账号"
                    )
                }
            )
            preference(
                key = "bind_qq_account",
                onClick = {
                    if ((model.bindQQState.toIntOrNull()?: -1) > 0) {
                        unbindQQUseState.show()
                    } else {
                        bindQQUseState.show()
                    }
                },
                title = { Text(text = "QQ号") },
                summary = {
                    Text(text = if ((model.bindQQState.toIntOrNull() ?: -1) > 0) "已绑定QQ: ${model.bindQQState}" else "未绑定")
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.ZoomIn,
                        contentDescription = "绑定QQ号"
                    )
                }
            )
            preference(
                key = "logout",
                onClick = {
                    model.showLogoutAlert = true
                },
                title = { Text("登出", color = MaterialTheme.colorScheme.error) },
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "登出",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            )
        }
    }

    InputDialog(
        state = bindFishUseState,
        selection = InputSelection(
            input = bindFishOption,
            onPositiveClick = { result ->
                scope.launch {
                    val username = result.getString("username")
                    val password = result.getString("password")
                    if (username == null || password == null) {
                        return@launch
                    }

                    val token = FishServer.getUserToken(username, password)
                    if (token.isEmpty()) {
                        scope.launch { snackbarHostState.showSnackbar("用户名或密码错误") }
                        return@launch
                    }

                    CFQServer.fishUploadToken(model.user.token, token)
                    model.updateFishTokenState(token)
                    scope.launch { snackbarHostState.showSnackbar("绑定成功！") }

                    bindFishUseState.invokeReset()
                }
            }
        ),
        header = Header.Default(title = "绑定水鱼网账号"),
    )

    InputDialog(
        state = bindQQUseState,
        selection = InputSelection(
            input = bindQQOption,
            onPositiveClick = { result ->
                val userQQ = result.getString("qq")
                if (userQQ == null) return@InputSelection

                scope.launch {
                    val userQQ = result.getString("qq")
                    if (userQQ == null) return@launch

                    if (!CFQServer.apiUserBindQQ(model.user.token, userQQ)) {
                        snackbarHostState.showSnackbar("绑定失败，该QQ已被其他账号绑定。")
                        return@launch
                    }
                    model.updateBindQQState(userQQ)
                    scope.launch { snackbarHostState.showSnackbar("绑定成功！") }
                }

                bindQQUseState.invokeReset()
            }
        ),
        header = Header.Default(title = "绑定QQ号")
    )

    InfoDialog(
        state = unbindFishUseState,
        body = unbindFishBody,
        selection = InfoSelection(
            onPositiveClick = {
                scope.launch {
                    CFQServer.fishUploadToken(model.user.token, "")
                    model.updateFishTokenState("")
                    snackbarHostState.showSnackbar("解绑成功！")
                }
            }
        ),
        header = Header.Default(title = "解绑水鱼网账号")
    )

    InfoDialog(
        state = unbindQQUseState,
        body = unbindQQBody,
        selection = InfoSelection(
            onPositiveClick = {
                scope.launch {
                    CFQServer.apiUserUnbindQQ(model.user.token)
                    model.updateBindQQState("")
                    snackbarHostState.showSnackbar("解绑成功！")
                }
            }
        ),
        header = Header.Default(title = "解绑QQ号")
    )
}