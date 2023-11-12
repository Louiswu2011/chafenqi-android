package com.nltv.chafenqi.view.premium

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nltv.chafenqi.SCREEN_PADDING
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumRedeemPage(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "会员兑换") },
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
        Column (
            Modifier.padding(paddingValues)
        ) {
            PremiumPerksPage(Modifier.weight(0.7f))
            Divider()
            Column (
                Modifier
                    .fillMaxWidth()
                    .weight(0.3f),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PremiumRedeemInputField(navController)
                PremiumRedeemInfo()
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PremiumPerksPage(modifier: Modifier) {
    val pageState = rememberPagerState(
        pageCount = { PREMIUM_PERKS.size }
    )

    Box (
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
    ) {
        HorizontalPager(
            state = pageState,
            modifier = Modifier.fillMaxHeight()
        ) { page ->
            val item = PREMIUM_PERKS[page]
            Column(
                modifier = Modifier
                    .padding(SCREEN_PADDING * 5)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(imageVector = item.icon, contentDescription = item.text)
                Text(text = item.title, style = MaterialTheme.typography.titleLarge)
                Text(text = item.text, style = MaterialTheme.typography.bodyLarge)
            }
        }

        Row(
            Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = SCREEN_PADDING),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pageState.pageCount) { iteration ->
                val color =
                    if (pageState.currentPage == iteration) Color.DarkGray else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PremiumRedeemInputField(navController: NavController) {
    val model: PremiumRedeemPageViewModel = viewModel()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    var redeemCode by remember {
        mutableStateOf("")
    }
    var isRedeemFailed by remember {
        mutableStateOf(false)
    }
    var showRedeemSuccessAlert by remember {
        mutableStateOf(false)
    }

    if (showRedeemSuccessAlert) {
        AlertDialog(
            onDismissRequest = { showRedeemSuccessAlert = false },
            confirmButton = {
                TextButton(onClick = {
                    model.user.isPremium = true
                    showRedeemSuccessAlert = false
                }) {
                    Text(text = "好的")
                }
            },
            title = { Text(text = "兑换成功") },
            text = { Text(text = "感谢您的购买，您的支持将会极大地帮助我们！") },
            icon = { Icon(imageVector = Icons.Default.Done, contentDescription = "兑换成功") }
        )
    }

    Column (
        Modifier
            .padding(SCREEN_PADDING)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = redeemCode,
            onValueChange = { redeemCode = it },
            label = { Text(text = "兑换码") },
            modifier = Modifier.fillMaxWidth(),
            isError = isRedeemFailed,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = false,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            ),
            maxLines = 1
        )
        Button(
            onClick = {
                scope.launch {
                    if (redeemCode.isEmpty()) {
                        Toast.makeText(context, "请输入兑换码", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                    redeemCode = redeemCode.filter { it.isWhitespace() }
                    try {
                        if (model.redeemMembership(redeemCode)) {
                            redeemCode = ""
                            showRedeemSuccessAlert = true
                            navController.navigateUp()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "兑换失败，请检查兑换码是否有效", Toast.LENGTH_LONG).show()
                    }
                }
                      },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(5.dp)
        ) {
            Text(text = "兑换")
        }
    }
}

@Composable
fun PremiumRedeemInfo() {
    val model: PremiumRedeemPageViewModel = viewModel()
    val uriHandler = LocalUriHandler.current

    Row (
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(SCREEN_PADDING),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        TextButton(onClick = { model.openPremiumPurchaseWebpage(uriHandler) }) {
            Icon(imageVector = Icons.Default.OpenInNew, contentDescription = "获取兑换码", Modifier.size(ButtonDefaults.IconSize))
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = "获取兑换码")
        }
        TextButton(onClick = { /*TODO*/ }) {
            Icon(imageVector = Icons.Default.CardGiftcard, contentDescription = "了解订阅功能", Modifier.size(ButtonDefaults.IconSize))
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = "了解订阅功能")
        }
    }
}