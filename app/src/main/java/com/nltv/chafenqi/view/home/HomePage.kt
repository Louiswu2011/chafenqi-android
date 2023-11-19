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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.nltv.chafenqi.LocalUserState
import com.nltv.chafenqi.SCREEN_PADDING
import com.nltv.chafenqi.storage.datastore.user.SettingsStore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun HomePage(navController: NavController) {
    val model: HomePageViewModel = viewModel<HomePageViewModel>().also { it.update() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val userState = LocalUserState.current
    val context = LocalContext.current
    val store = SettingsStore(context)
    val homeShowRefreshButton by store.homeShowRefreshButton.collectAsStateWithLifecycle(initialValue = false)
    
    val refreshState = rememberPullRefreshState(refreshing = userState.isRefreshing, onRefresh = { model.refreshUserData(userState) }, refreshThreshold = 120.dp)

    LaunchedEffect(Unit) {
        scope.launch {
            Firebase.crashlytics.setUserId(model.user.username)
            model.checkUpdates()
            model.saveCredentialsToCache(context)
        }
    }

    BackHandler(true) {
        // Prevent accidental back action when dragging rating indicators
    }

    if (model.showNewVersionDialog) {
        NewVersionDialog(onDismissRequest = { model.showNewVersionDialog = false }) {
            // TODO: Start new version download
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "主页") },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                actions = {
                    if (homeShowRefreshButton) {
                        IconButton(onClick = { model.refreshUserData(userState) }) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "刷新")
                        }
                    }
                    IconButton(onClick = { model.switchGame() }) {
                        Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = "切换游戏")
                    }
                    IconButton(onClick = { navController.navigate(HomeNavItem.Home.route + "/settings") }) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "设置")
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
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
                                HomePageRatingSelection(navController)
                                // TODO: Implement Logs
                                // HomePageLogBar(navController)
                                // HomePageLogInfo()
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

@Composable
fun NewVersionDialog(onDismissRequest: () -> Unit, onConfirmRequest: () -> Unit) {
    val model: HomePageViewModel = viewModel()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = { Button(onClick = onConfirmRequest) {
            Text(text = "更新")
        } },
        dismissButton = { Button(onClick = onDismissRequest) {
            Text(text = "忽略")
        } },
        icon = { Icon(imageVector = Icons.Default.Update, contentDescription = "发现新版本") },
        title = { Text(text = "发现新版本") },
        text = { Text(text = "当前版本为：${model.currentVersionCode} (${model.currentBuildNumber})" +
                "\n最新版本为：${model.latestVersionCode} (${model.latestBuildNumber}" +
                "\n是否更新？)") }
    )
}