package com.nltv.chafenqi.view.home.team.settings

import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.michaelflisar.composepreferences.core.PreferenceScreen
import com.michaelflisar.composepreferences.core.hierarchy.PreferenceRootScope
import com.nltv.chafenqi.view.home.HomeNavItem
import com.nltv.chafenqi.view.home.team.HomeTeamPageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTeamSettingsPendingMemberManagePage(navController: NavController) {
    val snackbarHostState = remember { SnackbarHostState() }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val model: HomeTeamPageViewModel = viewModel(
        viewModelStoreOwner = navBackStackEntry?.let { navController.getBackStackEntry(HomeNavItem.Home.route + "/team") }
            ?: LocalViewModelStoreOwner.current!!
    )

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
        PreferenceScreen (
            modifier = Modifier.padding(paddingValues)
        ) {
            TeamPendingMemberManageSettings(
                navController = navController,
                snackbarHostState = snackbarHostState
            )
        }
    }
}

@Composable
fun PreferenceRootScope.TeamPendingMemberManageSettings(
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {}