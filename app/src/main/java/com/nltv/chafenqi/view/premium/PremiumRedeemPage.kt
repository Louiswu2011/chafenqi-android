package com.nltv.chafenqi.view.premium

import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.nltv.chafenqi.view.home.HomeNavItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumRedeemPage(navController: NavController) {
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val pageState = rememberPagerState(
        pageCount = { PREMIUM_PERKS.size }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "会员兑换") },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回上一级"
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.6f)
                    .padding(paddingValues)
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

            Column(
                Modifier
                    .fillMaxWidth()
                    .weight(0.4f),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PremiumRedeemInputField(navController, snackbarHostState)
            }
        }
    }
}

@Composable
fun PremiumRedeemInputField(navController: NavController, snackbarHostState: SnackbarHostState) {
    val model: PremiumRedeemPageViewModel = viewModel()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val uriHandler = LocalUriHandler.current

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

    Column(
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
        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    scope.launch {
                        if (redeemCode.isEmpty()) {
                            snackbarHostState.showSnackbar("请输入兑换码")
                            return@launch
                        }
                        redeemCode = redeemCode.filter { !it.isWhitespace() }
                        try {
                            if (model.redeemMembership(redeemCode)) {
                                redeemCode = ""
                                showRedeemSuccessAlert = true
                                navController.navigateUp()
                            }
                        } catch (e: Exception) {
                            Log.e(
                                "PremiumRedeem",
                                "Failed to validate redeem code $redeemCode, error: ${e.localizedMessage}"
                            )
                            snackbarHostState.showSnackbar("兑换失败，请检查兑换码是否有效")
                        }
                    }
                },
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "兑换")
            }
            Spacer(Modifier.width(10.dp))
            Button(
                onClick = { model.openPremiumPurchaseWebpage(uriHandler) },
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "获取兑换码")
            }
        }
    }
}