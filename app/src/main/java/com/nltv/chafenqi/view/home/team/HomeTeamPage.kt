package com.nltv.chafenqi.view.home.team

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maxkeppeker.sheets.core.models.base.Header
import com.maxkeppeker.sheets.core.models.base.IconSource
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.info.InfoDialog
import com.maxkeppeler.sheets.info.models.InfoBody
import com.maxkeppeler.sheets.info.models.InfoSelection
import com.nltv.chafenqi.networking.CFQTeamServer
import com.nltv.chafenqi.view.home.HomeNavItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTeamLandingPage(navController: NavController) {
    val model: HomeTeamPageViewModel = viewModel()
    val state by model.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        model.refresh()
    }

    Crossfade(targetState = state.isLoading, label = "Team loading cross fade") {
        when (it) {
            true -> {
                Scaffold (
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
                        )
                    },
                ) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                    }
                }

            }
            false -> {
                when (state.currentTeamId) {
                    null -> {
                        HomeTeamIntroductionPage(navController)
                    }
                    else -> {
                        HomeTeamPage(navController)
                    }
                }
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
    val scope = rememberCoroutineScope()

    var expanded by rememberSaveable { mutableStateOf(false) }
    var selectedTabIndex by rememberSaveable {
        mutableIntStateOf(0)
    }
    val pagerState = rememberPagerState {
        model.tabs.size
    }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }

    LaunchedEffect(pagerState.targetPage) {
        selectedTabIndex = pagerState.targetPage
    }

    var showBulletinComposeSheet by remember { mutableStateOf(false) }

    val leaveTeamConfirmUseCase = rememberUseCaseState()

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
                    IconButton(onClick = {
                        scope.launch(Dispatchers.IO) {
                            model.refresh()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                        )
                    }
                    if (state.isTeamAdmin) {
                        IconButton(onClick = {
                            navController.navigate(HomeNavItem.Home.route + "/team/settings")
                        })
                        {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                            )
                        }
                    } else {
                        IconButton(onClick = {
                            leaveTeamConfirmUseCase.show()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.Logout,
                                contentDescription = "Leave Team"
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            AnimatedContent(pagerState.currentPage, label = "team FAB switching") {
                when (it) {
                    3 -> {
                        ExtendedFloatingActionButton(
                            text = { Text("发布留言") },
                            icon = { Icon(Icons.Default.AddComment, contentDescription = "发布留言") },
                            onClick = {
                                showBulletinComposeSheet = true
                            }
                        )
                    }
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Column (
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
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
                    0 -> {
                        HomeTeamPageMemberList(
                            snackbarHostState = snackbarHostState
                        )
                    }

                    1 -> {
                        HomeTeamPageActivitySection()
                    }

                    2 -> {
                        HomeTeamPageCourseSection()
                    }

                    3 -> {
                        HomeTeamPageBulletinBoardSection(snackbarHostState)
                    }
                }
            }
        }

    }

    if (showBulletinComposeSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBulletinComposeSheet = false },
            sheetState = sheetState
        ) {
            BulletinComposeSheet (
                snackbarHostState = snackbarHostState,
                onDismiss = { showBulletinComposeSheet = false }
            ) { content ->
                CFQTeamServer.addTeamBulletinBoardEntry(
                    authToken = model.token,
                    game = model.mode,
                    teamId = state.team.info.id,
                    message = content
                )
            }
        }
    }

    InfoDialog(
        state = leaveTeamConfirmUseCase,
        header = Header.Default(
            title = "离开团队",
            icon = IconSource(imageVector = Icons.AutoMirrored.Default.Logout)
        ),
        body = InfoBody.Default(
            bodyText = "确定要离开该团队吗？",
        ),
        selection = InfoSelection(
            onPositiveClick = {
                scope.launch(Dispatchers.IO) {
                    val result = CFQTeamServer.leaveTeam(
                        authToken = model.token,
                        game = model.mode,
                        teamId = state.team.info.id
                    )
                    if (result.isEmpty()) {
                        navController.navigateUp()
                    } else {
                        snackbarHostState.showSnackbar("离开团队失败，$result")
                    }
                }
            }
        )
    )
}