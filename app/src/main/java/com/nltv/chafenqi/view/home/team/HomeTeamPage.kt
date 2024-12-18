package com.nltv.chafenqi.view.home.team

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun HomeTeamLandingPage(navController: NavController) {
    val model: HomeTeamPageViewModel = viewModel()
    val state by model.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        model.refresh()
    }

    Crossfade(targetState = state.currentTeamId, label = "Team landing page cross fade") {
        when (it) {
            null -> {
                HomeTeamIntroductionPage(navController)
            }
            else -> {
                HomeTeamPage(navController)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTeamPage(navController: NavController) {
    val model: HomeTeamPageViewModel = viewModel()
    val state by model.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var expanded by rememberSaveable { mutableStateOf(false) }
    var selectedTabIndex by rememberSaveable {
        mutableIntStateOf(0)
    }
    val pagerState = rememberPagerState {
        model.tabs.size
    }
    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }

    LaunchedEffect(pagerState.targetPage) {
        selectedTabIndex = pagerState.targetPage
    }

    LaunchedEffect(Unit) {
        model.refresh()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "团队") },
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
                },
                actions = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More",
                        )
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(
                                text = { Text("退出团队...") },
                                colors = MenuDefaults.itemColors(
                                    textColor = MaterialTheme.colorScheme.error,
                                    leadingIconColor = MaterialTheme.colorScheme.error
                                ),
                                leadingIcon = { Icon(imageVector = Icons.AutoMirrored.Default.Logout, contentDescription = "退出团队") },
                                onClick = {
                                    expanded = false
                                })
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (pagerState.currentPage == 3) {
                ExtendedFloatingActionButton(
                    text = { Text("发布留言") },
                    icon = { Icon(Icons.Default.AddComment, contentDescription = "发布留言") },
                    onClick = {}
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            HomeTeamPageInfoSection(snackbarHostState)

            TabRow(
                selectedTabIndex = selectedTabIndex
            ) {
                model.tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(text = tab.title) },
                        icon = {
                            Icon(
                                imageVector = if (selectedTabIndex == index) tab.iconSelected else tab.icon,
                                contentDescription = tab.title
                            )
                        }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { index ->
                when (index) {
                    0 -> { HomeTeamPageMemberList() }
                    1 -> {}
                    2 -> { HomeTeamPageCourseSection() }
                    3 -> { HomeTeamPageBulletinBoardSection() }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTeamPageConfirmDialog(
    icon: ImageVector,
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        icon = { Icon(imageVector = icon, contentDescription = title) },
        title = { Text(text = title) },
        text = { Text(text = message) },
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text("确认")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text("取消")
            }
        }
    )
}