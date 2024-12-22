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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.nltv.chafenqi.LocalUserState
import com.nltv.chafenqi.SCREEN_PADDING
import com.nltv.chafenqi.storage.SettingsStore
import com.nltv.chafenqi.view.module.AppUpdaterDialog
import com.nltv.chafenqi.view.module.AppUpdaterViewModel
import me.zhanghai.compose.preference.LocalPreferenceFlow

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class
)
@Composable
fun HomePage(navController: NavController) {
    val model: HomePageViewModel = viewModel<HomePageViewModel>().also { it.update() }
    val uiState by model.uiState.collectAsStateWithLifecycle()
    val updaterModel: AppUpdaterViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val userState = LocalUserState.current
    val context = LocalContext.current
    val store = SettingsStore(context)
    val settings by LocalPreferenceFlow.current.collectAsStateWithLifecycle()

    val homeArrangement by store.homeArrangement.collectAsStateWithLifecycle(initialValue = "最近动态|Rating分析|排行榜|出勤记录")

    val refreshState = rememberPullRefreshState(
        refreshing = userState.isRefreshing,
        onRefresh = { model.refreshUserData(userState, context) },
        refreshThreshold = 120.dp
    )
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        updaterModel.checkUpdates(snackbarHostState, true)
        Firebase.crashlytics.setUserId(model.user.username)
        model.checkUpdates()
        model.saveCredentialsToCache(context)
        if (!model.isLoaded) {
            model.switchGame(settings.get<Int>("homeDefaultGame") ?: 0)
            model.isLoaded = true
        }
    }


    BackHandler(true) {
        // Prevent accidental back action when dragging rating indicators
    }

    AppUpdaterDialog(snackbarHostState = snackbarHostState)

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
                    IconButton(onClick = { model.switchGame() }) {
                        Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = "切换游戏")
                    }
                    /*IconButton(onClick = { navController.navigate(HomeNavItem.Home.route + "/announcement") }) {
                        Icon(imageVector = Icons.Default.Notifications, contentDescription = "应用内通知")
                    }*/
                    IconButton(onClick = { navController.navigate(HomeNavItem.Home.route + "/settings") }) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "设置")
                    }
                },
                navigationIcon = {
                    if (settings.get<Boolean>("homeShowRefreshButton") == true) {
                        IconButton(onClick = { model.refreshUserData(userState, context) }) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "刷新")
                        }
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            Modifier.pullRefresh(refreshState, !userState.isRefreshing)
        ) {
            Crossfade(targetState = userState.isRefreshing, label = "refresh animation") {
                if (it) {
                    Column(
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
                            HomePageNameplateSection(navController)

                            if (settings.get<Boolean>("homeShowTeamButton") == true) {
                                HomePageTeamSection(navController)
                            }

                            homeArrangement.split("|").forEach { item ->
                                when (item) {
                                    "最近动态" -> HomePageRecentSection(navController)
                                    "Rating分析" -> HomePageRatingSection(navController)
                                    "排行榜" -> HomePageLeaderboardSection(navController)
                                    "出勤记录" -> HomePageLogSection(navController)
                                }
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
    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "数据为空警告",
            Modifier.padding(
                SCREEN_PADDING
            )
        )
        Text(text = "未找到玩家数据", Modifier.padding(bottom = SCREEN_PADDING))
        Text(text = "请先进行一次传分后下拉刷新")
    }
}