package com.nltv.chafenqi.view.home.team.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.nltv.chafenqi.extension.toDateString
import com.nltv.chafenqi.extension.toHalfWidth
import com.nltv.chafenqi.model.team.TeamPendingMember
import com.nltv.chafenqi.networking.CFQTeamServer
import com.nltv.chafenqi.view.home.HomeNavItem
import com.nltv.chafenqi.view.home.team.HomeTeamPageViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTeamSettingsPendingMemberManagePage(navController: NavController) {
    val snackbarHostState = remember { SnackbarHostState() }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val model: HomeTeamPageViewModel = viewModel(
        viewModelStoreOwner = navBackStackEntry?.let { navController.getBackStackEntry(HomeNavItem.Home.route + "/team") }
            ?: LocalViewModelStoreOwner.current!!
    )
    val state by model.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    Scaffold (
        topBar = {
            LargeTopAppBar(
                title = { Text("待加入成员") },
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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        if (state.team.pendingMembers.isEmpty()) {
            Column (
                modifier = Modifier.padding(paddingValues)
                   .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("暂无待加入成员")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(paddingValues)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    count = state.team.pendingMembers.size,
                    key = { index -> state.team.pendingMembers[index].userId },
                ) { index ->
                    val pendingMember = state.team.pendingMembers[index]
                    HomeTeamSettingsPendingMemberEntry(
                        pendingMember = pendingMember,
                        onAccept = {
                            scope.launch(Dispatchers.IO) {
                                val result = CFQTeamServer.adminAcceptMember(
                                    authToken = model.token,
                                    game = model.mode,
                                    teamId = state.team.info.id,
                                    pendingMemberId = pendingMember.userId
                                )

                                if (result.isEmpty()) {
                                    snackbarHostState.showSnackbar("已同意加入")
                                    model.refresh()
                                } else {
                                    snackbarHostState.showSnackbar("同意加入失败，请联系开发者")
                                }
                            }
                        },
                        onReject = {
                            scope.launch(Dispatchers.IO) {
                                val result = CFQTeamServer.adminRejectMember(
                                    authToken = model.token,
                                    game = model.mode,
                                    teamId = state.team.info.id,
                                    pendingMemberId = pendingMember.userId
                                )

                                if (result.isEmpty()) {
                                    snackbarHostState.showSnackbar("已拒绝加入")
                                    model.refresh()
                                } else {
                                    snackbarHostState.showSnackbar("拒绝加入失败，请联系开发者")
                                }
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun HomeTeamSettingsPendingMemberEntry(
    pendingMember: TeamPendingMember,
    onAccept: () -> Unit,
    onReject: () -> Unit,
) {
    val context = LocalContext.current

    var confirmAccept by remember { mutableStateOf(false) }
    var confirmReject by remember { mutableStateOf(false) }

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column (
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(pendingMember.nickname.toHalfWidth())
            Text("加入时间：${pendingMember.timestamp.toDateString(context)}")
        }

        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TextButton(
                onClick = {
                    if (confirmAccept) {
                        onAccept()
                    }

                    confirmAccept = true
                    confirmReject = false
                }
            ) {
                Text(text = if (confirmAccept) "确认接受" else "接受")
            }

            TextButton(
                onClick = {
                    if (confirmReject) {
                        onReject()
                    }

                    confirmReject = true
                    confirmAccept = false
                }
            ) {
                Text(text = if (confirmReject) "确认拒绝" else "拒绝")
            }
        }
    }
}