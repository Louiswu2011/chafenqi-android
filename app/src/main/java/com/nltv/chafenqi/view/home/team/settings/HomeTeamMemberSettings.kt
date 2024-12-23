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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.michaelflisar.composepreferences.core.PreferenceScreen
import com.michaelflisar.composepreferences.core.hierarchy.PreferenceRootScope
import com.nltv.chafenqi.extension.toDateString
import com.nltv.chafenqi.extension.toHalfWidth
import com.nltv.chafenqi.model.team.TeamMember
import com.nltv.chafenqi.view.home.HomeNavItem
import com.nltv.chafenqi.view.home.team.HomeTeamPageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTeamSettingsMemberManagePage(navController: NavController) {
    val snackbarHostState = remember { SnackbarHostState() }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val model: HomeTeamPageViewModel = viewModel(
        viewModelStoreOwner = navBackStackEntry?.let { navController.getBackStackEntry(HomeNavItem.Home.route + "/team") }
            ?: LocalViewModelStoreOwner.current!!
    )
    val state by model.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        model.refresh()
    }

    Scaffold (
        topBar = {
            LargeTopAppBar(
                title = { Text("成员") },
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
        LazyColumn (
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ) {
            items(
                count = state.team.members.size,
                key = { index -> state.team.members[index].userId },
            ) { index ->
                val member = state.team.members[index]
                HomeTeamSettingsMemberEntry(
                    member = member,
                    isLeader = member.userId == state.team.info.leaderUserId,
                    onKick = {  },
                    onTransfer = {  }
                )
            }
        }
    }
}

@Composable
fun HomeTeamSettingsMemberEntry(
    member: TeamMember,
    isLeader: Boolean,
    onKick: () -> Unit,
    onTransfer: () -> Unit,
) {
    val context = LocalContext.current

    var confirmTransfer by remember { mutableStateOf(false) }
    var confirmKick by remember { mutableStateOf(false) }

    Row (
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column (
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(member.nickname.toHalfWidth())
            Text("加入时间：${member.joinAt.toDateString(context)}")
        }

        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TextButton(
                onClick = {
                    if (confirmTransfer) {

                    }

                    confirmTransfer = true
                    confirmKick = false
                },
                enabled = !isLeader
            ) {
                Text(text = if (confirmTransfer) "确认转让" else "转让")
            }

            TextButton(
                onClick = {
                    if (confirmKick) {

                    }

                    confirmKick = true
                    confirmTransfer = false
                },
                enabled = !isLeader
            ) {
                Text(text = if (confirmKick) "确认移除" else "移除", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}