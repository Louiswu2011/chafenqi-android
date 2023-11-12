package com.nltv.chafenqi.view.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.pullRefreshIndicatorTransform
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nltv.chafenqi.LocalUserState
import com.nltv.chafenqi.SCREEN_PADDING
import dev.burnoo.compose.rememberpreference.rememberIntPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun HomePage(navController: NavController) {
    val model: HomePageViewModel = viewModel<HomePageViewModel>().also { it.update() }
    val scrollState = rememberScrollState()
    val userState = LocalUserState.current

    val refreshState = rememberPullRefreshState(refreshing = userState.isRefreshing, onRefresh = { model.refreshUserData(userState) }, refreshThreshold = 120.dp)

    BackHandler(true) {
        // Prevent accidental back action when dragging rating indicators
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "主页") },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    IconButton(onClick = { model.switchGame() }) {
                        Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = "切换游戏")
                    }
                    IconButton(onClick = { navController.navigate(HomeNavItem.Home.route + "/settings") }) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "设置")
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            Modifier.pullRefresh(refreshState, !userState.isRefreshing)
        ) {
            Crossfade(targetState = userState.isRefreshing, label = "refresh animation") {
                if (it) {
                    Column (
                        Modifier
                            .padding(paddingValues)
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(SCREEN_PADDING * 2))
                        Text(text = "刷新中...")
                    }
                } else {
                    Column(
                        Modifier
                            .padding(paddingValues)
                            .verticalScroll(scrollState),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if ((model.user.mode == 1 && model.user.maimai.isBasicEmpty) || (model.user.mode == 0 && model.user.chunithm.isBasicEmpty)) {
                            EmptyDataPage()
                        } else {
                            HomePageNameplate(navController)
                            HomePageRecentBar(navController)
                            HomePageRecentLineup(navController)
                            HomePageRatingBar(navController)
                            if (model.user.isPremium) {
                                HomePageRatingIndicators()
                                HomePageRatingSelection()
                                HomePageLogBar(navController)
                                HomePageLogInfo()
                            }
                        }
                    }
                }
            }
            PullRefreshIndicator(
                refreshing = userState.isRefreshing,
                state = refreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
fun EmptyDataAlert(onDismissRequest: () -> Unit, onConfirmRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirmRequest) {
                Text(text = "好的")
            }
        },
        icon = {
            Icon(imageVector = Icons.Default.Warning, contentDescription = "数据为空警告")
        },
        title = {
            Text(text = "数据不存在")
        },
        text = {
            Text(text = "首次订阅后，请先进行一次传分以同步玩家信息")
        }
    )
}

@Composable
fun EmptyDataPage() {
    Column (
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = Icons.Default.Warning, contentDescription = "数据为空警告", Modifier.padding(
            SCREEN_PADDING))
        Text(text = "未找到玩家数据", Modifier.padding(bottom = SCREEN_PADDING))
        Text(text = "请先进行一次传分后下拉刷新")
    }
}