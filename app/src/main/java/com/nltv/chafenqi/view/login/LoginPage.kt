package com.nltv.chafenqi.view.login

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
import com.nltv.chafenqi.view.AppViewModelProvider

@Composable
fun LoginPage() {
    val model: LoginPageViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val loginUiState by model.loginUiState.collectAsStateWithLifecycle()

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

    var username by remember {
        mutableStateOf("testaccount")
    }
    var password by remember {
        mutableStateOf("testtest")
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
                if (loginUiState.loginState == UIState.Pending) {
                    model.login(username, password.sha256(), context, userState)
                }
            },
            modifier = Modifier.padding(top = 30.dp)
        ) {
            Text(text = "登录")
        }
        TextButton(
            onClick = {

            }
        ) {
            Text(text = "注册新账号", color = MaterialTheme.colorScheme.primary)
        }
        TextButton(
            onClick = {
                model.clearPersistentStorage(context)
            }
        ) {
            Text(text = "清除缓存", color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAppIconWithFrame() {
    AppIconWithFrame()
}