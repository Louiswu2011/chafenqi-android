package com.nltv.chafenqi.view.login

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nltv.chafenqi.LocalUserState
import com.nltv.chafenqi.R
import com.nltv.chafenqi.UIState
import com.nltv.chafenqi.extension.sha256
import com.nltv.chafenqi.networking.CFQServer
import com.nltv.chafenqi.networking.CFQServerSideException
import com.nltv.chafenqi.networking.CredentialsMismatchException
import com.nltv.chafenqi.networking.UserNotFoundException
import com.nltv.chafenqi.networking.UsernameOccupiedException
import com.nltv.chafenqi.storage.datastore.user.SettingsStore
import com.nltv.chafenqi.storage.datastore.user.SettingsStore.Companion.settingsStore
import kotlinx.coroutines.launch

@Composable
fun LoginPage() {
    val model: LoginPageViewModel = viewModel()
    val context = LocalContext.current
    val userState = LocalUserState.current
    val loginUiState by model.loginUiState.collectAsStateWithLifecycle()


    LaunchedEffect(Unit) {
        val credentials = model.getCachedCredentials(context)
        if (credentials.size < 2) { return@LaunchedEffect }

        val token = credentials[0]
        val username = credentials[1]

        if (token.isEmpty() || username.isEmpty()) { return@LaunchedEffect }
        Log.i("Login", "Cached username: $username, token: $token")

        try {
            model.login(token, username, context, userState, loadFromCache = true)
        } catch (e: Exception) {
            Log.e("Login", "Error login from cached token, error: ${e.localizedMessage}")
            Toast.makeText(context, "登陆状态失效，请重试", Toast.LENGTH_LONG).show()
        }
    }

    Column(
        Modifier
            .padding(all = 8.dp)
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppIconWithFrame()
        Spacer(modifier = Modifier.padding(all = 30.dp))

        AnimatedContent(
            targetState = loginUiState.loginState,
            label = "LoginScreenAnimatedContent"
        ) {
            when (it) {
                UIState.Pending -> {
                    LoginField(model)
                }

                UIState.Loading -> {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Text(text = loginUiState.loginPromptText, modifier = Modifier.padding(8.dp))
                    }
                }

                UIState.Finished -> {

                }
            }
        }
    }
}

@Composable
fun AppIconWithFrame() {
    val borderWidth = 5.dp
    val innerBorderWidth = 3.dp
    val cornerSize = 20.dp

    val rainbowColorsBrush = remember {
        Brush.sweepGradient(
            listOf(
                Color(0xFF9575CD),
                Color(0xFFBA68C8),
                Color(0xFFE57373),
                Color(0xFFFFB74D),
                Color(0xFFFFF176),
                Color(0xFFAED581),
                Color(0xFF4DD0E1),
                Color(0xFF9575CD)
            )
        )
    }

    val appIconModifier = Modifier
        .size(120.dp)
        .border(
            BorderStroke(width = borderWidth, brush = rainbowColorsBrush),
            RoundedCornerShape(size = cornerSize)
        )
        .padding(innerBorderWidth)
        .clip(RoundedCornerShape(size = cornerSize))

    Image(
        painter = painterResource(id = R.drawable.app_icon),
        contentDescription = "AppIcon",
        contentScale = ContentScale.Crop,
        modifier = appIconModifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginField(model: LoginPageViewModel) {
    val loginUiState by model.loginUiState.collectAsStateWithLifecycle()
    val userState = LocalUserState.current

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val store = SettingsStore(context)
    val defaultGame by store.homeDefaultGame.collectAsStateWithLifecycle(initialValue = 1)

    var registerMode by remember {
        mutableStateOf(false)
    }
    var username by remember {
        mutableStateOf(context.getString(R.string.username))
    }
    var password by remember {
        mutableStateOf(context.getString(R.string.password))
    }
    var passwordVisible by rememberSaveable {
        mutableStateOf(false)
    }

    val usernameTextFieldKeyboardOptions = KeyboardOptions(
        capitalization = KeyboardCapitalization.None,
        autoCorrect = false,
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Next
    )
    val passwordTextFieldKeyboardOptions = KeyboardOptions(
        capitalization = KeyboardCapitalization.None,
        autoCorrect = false,
        keyboardType = KeyboardType.Password,
        imeAction = ImeAction.Done
    )

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(text = "用户名") },
            keyboardOptions = usernameTextFieldKeyboardOptions
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "密码") },
            keyboardOptions = passwordTextFieldKeyboardOptions,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else
                    Icons.Filled.VisibilityOff
                val description = if (passwordVisible) "Hide Password" else "Show Password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(image, description)
                }
            }
        )
        Button(
            onClick = {
                if (registerMode) {
                    if (username.isEmpty() || password.isEmpty()) {
                        Toast.makeText(context, "请输入用户名和密码", Toast.LENGTH_LONG).show()
                        return@Button
                    }
                    scope.launch {
                        try {
                            if (CFQServer.authRegister(username, password.sha256())) {
                                Toast.makeText(context, "注册成功", Toast.LENGTH_SHORT).show()
                                registerMode = false
                            }
                        } catch (e: UsernameOccupiedException) {
                            Toast.makeText(context, "该用户名已被占用", Toast.LENGTH_LONG).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "出错了：${e.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    if (loginUiState.loginState == UIState.Pending) {
                        try {
                            model.login(username, password.sha256(), context, userState)
                            model.user.mode = defaultGame
                        } catch (e: Exception) {
                            when (e) {
                                is CredentialsMismatchException,
                                is UserNotFoundException -> {
                                    Toast.makeText(context, "用户名或密码错误", Toast.LENGTH_LONG).show()
                                }
                                is CFQServerSideException -> {
                                    Toast.makeText(context, "服务器出错，请稍后再试", Toast.LENGTH_LONG).show()
                                }
                                else -> {
                                    Toast.makeText(context, "未知错误: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                }
            },
            modifier = Modifier.padding(top = 30.dp)
        ) {
            Text(text = if (registerMode) "注册" else "登录")
        }
        TextButton(
            onClick = {
                registerMode = !registerMode
            }
        ) {
            Text(text = if (!registerMode) "注册新账号" else "登录已有账号", color = MaterialTheme.colorScheme.primary)
        }
        /*TextButton(
            onClick = {
                model.clearPersistentStorage(context)
            }
        ) {
            Text(text = "清除缓存", color = MaterialTheme.colorScheme.primary)
        }*/
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAppIconWithFrame() {
    AppIconWithFrame()
}