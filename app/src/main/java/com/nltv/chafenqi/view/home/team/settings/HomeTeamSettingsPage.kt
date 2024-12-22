package com.nltv.chafenqi.view.home.team.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.michaelflisar.composepreferences.core.PreferenceDivider
import com.michaelflisar.composepreferences.core.PreferenceInfo
import com.michaelflisar.composepreferences.core.PreferenceScreen
import com.michaelflisar.composepreferences.core.classes.Dependency
import com.michaelflisar.composepreferences.core.classes.PreferenceSettingsDefaults
import com.michaelflisar.composepreferences.core.hierarchy.PreferenceGroupItem
import com.michaelflisar.composepreferences.core.hierarchy.PreferenceRootScope
import com.michaelflisar.composepreferences.screen.button.PreferenceButton
import com.michaelflisar.composepreferences.screen.input.PreferenceInputText
import com.nltv.chafenqi.view.home.team.HomeTeamPageViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.michaelflisar.composepreferences.core.classes.PreferenceStyleDefaults
import com.michaelflisar.composepreferences.screen.bool.PreferenceBool
import com.nltv.chafenqi.extension.toDateString
import com.nltv.chafenqi.networking.CFQTeamServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTeamPageSettingsPage(navController: NavController) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold (
        topBar = {
            LargeTopAppBar(
                title = { Text("团队设置") },
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
            settings = PreferenceSettingsDefaults.settings(),
            scrollable = true,
            modifier = Modifier.padding(paddingValues)
        ) {
            TeamSettings(snackbarHostState = snackbarHostState)
        }
    }
}

@Composable
fun PreferenceRootScope.TeamSettings(
    snackbarHostState: SnackbarHostState
) {
    val model: HomeTeamPageViewModel = viewModel()
    val state by model.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var teamName by remember { mutableStateOf(state.team.info.displayName) }
    var teamStyle by remember { mutableStateOf(state.team.info.style) }
    var teamRemarks by remember { mutableStateOf(state.team.info.remarks) }
    var teamPromotable by remember { mutableStateOf(state.team.info.promotable) }

    LaunchedEffect(teamName) {
        if (teamName != state.team.info.displayName) {
            scope.launch(Dispatchers.IO) {
                val result = CFQTeamServer.adminUpdateTeamName(
                    authToken = model.token,
                    game = model.mode,
                    teamId = state.team.info.id,
                    newName = teamName
                )
                if (result.isNotEmpty()) {
                    snackbarHostState.showSnackbar("修改团队名称失败：$result")
                    teamName = state.team.info.displayName
                } else {
                    model.refresh()
                }
            }
        }
    }

    LaunchedEffect(teamStyle) {
        if (teamStyle!= state.team.info.style) {
            scope.launch(Dispatchers.IO) {
                val result = CFQTeamServer.adminUpdateTeamStyle(
                    authToken = model.token,
                    game = model.mode,
                    teamId = state.team.info.id,
                    newStyle = teamStyle
                )
                if (!result) {
                    snackbarHostState.showSnackbar("修改团队风格失败，请联系开发者")
                    teamStyle = state.team.info.style
                } else {
                    model.refresh()
                }
            }
        }
    }

    LaunchedEffect(teamRemarks) {
        if (teamRemarks!= state.team.info.remarks) {
            scope.launch(Dispatchers.IO) {
                val result = CFQTeamServer.adminUpdateTeamRemarks(
                    authToken = model.token,
                    game = model.mode,
                    teamId = state.team.info.id,
                    newRemarks = teamRemarks
                )
                if (!result) {
                    snackbarHostState.showSnackbar("修改团队备注失败，请联系开发者")
                    teamRemarks = state.team.info.remarks
                } else {
                    model.refresh()
                }
            }
        }
    }

    LaunchedEffect(teamPromotable) {
        if (teamPromotable!= state.team.info.promotable) {
            scope.launch(Dispatchers.IO) {
                val result = CFQTeamServer.adminUpdateTeamPromotable(
                    authToken = model.token,
                    game = model.mode,
                    teamId = state.team.info.id,
                    promotable = teamPromotable
                )
                if (!result) {
                    snackbarHostState.showSnackbar("修改团队可见失败，请联系开发者")
                    teamPromotable = state.team.info.promotable
                } else {
                    model.refresh()
                }
            }
        }
    }

    PreferenceInfo(
        title = { Text("基本信息") },
    )
    PreferenceInputText(
        value = teamName,
        onValueChange = {
            teamName = it
        },
        title = { Text("团队名称") },
        subtitle = { Text("最近修改时间：${state.team.info.nameLastModifiedAt.toDateString(context)}") },
    )
    PreferenceInputText(
        value = teamStyle,
        onValueChange = {
            teamStyle = it
        },
        title = { Text("团队风格") }
    )
    PreferenceInputText(
        value = teamRemarks,
        onValueChange = {
            teamRemarks = it
        },
        title = { Text("团队介绍") }
    )
    PreferenceBool(
        value = teamPromotable,
        onValueChange = {
            teamPromotable = it
        },
        title = { Text("可被搜索") }
    )

    PreferenceInfo(
        title = { Text("其他设置") },
    )
    PreferenceButton(
        onClick = {
            // Show confirm dialog
            scope.launch(Dispatchers.IO) {
                val result = CFQTeamServer.adminRotateTeamCode(
                    authToken = model.token,
                    game = model.mode,
                    teamId = state.team.info.id
                )

                if (!result) {
                    snackbarHostState.showSnackbar("重新生成团队代码失败，请联系开发者")
                } else {
                    model.refresh()
                }
            }
        },
        title = { Text("重新生成团队代码") }
    )
    PreferenceButton(
        onClick = {
            // Show confirm dialog
        },
        title = { Text("解散团队", color = MaterialTheme.colorScheme.error) }
    )
}