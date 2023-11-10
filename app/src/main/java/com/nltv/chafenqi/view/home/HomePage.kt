package com.nltv.chafenqi.view.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(navController: NavController) {
    val model: HomePageViewModel = viewModel<HomePageViewModel>().also { it.update() }
    val scrollState = rememberScrollState()
    // val pullRefreshState = rememberPullRefreshState(refreshing = , onRefresh = { /*TODO*/ })

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
    ) {
        Column(
            Modifier
                .padding(it)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
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