package com.nltv.chafenqi.view.home.team.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Ballot
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.maxkeppeker.sheets.core.models.base.Header
import com.maxkeppeker.sheets.core.models.base.IconSource
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.info.InfoDialog
import com.maxkeppeler.sheets.info.models.InfoBody
import com.maxkeppeler.sheets.info.models.InfoSelection
import com.maxkeppeler.sheets.input.InputDialog
import com.maxkeppeler.sheets.input.models.InputHeader
import com.maxkeppeler.sheets.input.models.InputSelection
import com.maxkeppeler.sheets.input.models.InputTextField
import com.michaelflisar.composepreferences.core.PreferenceDivider
import com.michaelflisar.composepreferences.core.PreferenceInfo
import com.michaelflisar.composepreferences.core.PreferenceScreen
import com.michaelflisar.composepreferences.core.PreferenceSectionHeader
import com.michaelflisar.composepreferences.core.PreferenceSubScreen
import com.michaelflisar.composepreferences.core.classes.PreferenceStyleDefaults
import com.michaelflisar.composepreferences.core.hierarchy.PreferenceRootScope
import com.michaelflisar.composepreferences.core.hierarchy.PreferenceScope
import com.michaelflisar.composepreferences.screen.bool.PreferenceBool
import com.michaelflisar.composepreferences.screen.button.PreferenceButton
import com.michaelflisar.composepreferences.screen.list.PreferenceList
import com.nltv.chafenqi.extension.toDateString
import com.nltv.chafenqi.networking.CFQTeamServer
import com.nltv.chafenqi.view.home.HomeNavItem
import com.nltv.chafenqi.view.home.team.HomeTeamPageViewModel
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
            modifier = Modifier.padding(paddingValues)
        ) {
            TeamSettings(
                navController = navController,
                snackbarHostState = snackbarHostState
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenceRootScope.TeamSettings(
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val model: HomeTeamPageViewModel = viewModel(
        viewModelStoreOwner = navBackStackEntry?.let { navController.getBackStackEntry(HomeNavItem.Home.route + "/team") }
            ?: LocalViewModelStoreOwner.current!!
    )
    val state by model.uiState.collectAsStateWithLifecycle()

    val editTeamNameUseCase = rememberUseCaseState()
    val editTeamStyleUseCase = rememberUseCaseState()
    val editTeamRemarksUseCase = rememberUseCaseState()

    val confirmRefreshTeamCodeUseCase = rememberUseCaseState()
    val confirmDeleteTeamUseCase = rememberUseCaseState()

    val editTeamNameInputs = listOf(
        InputTextField(
            header = InputHeader(
                title = "新团队名称"
            ),
            key = "newTeamName"
        )
    )
    val editTeamStyleInputs = listOf(
        InputTextField(
            header = InputHeader(
                title = "新团队方针"
            ),
            key = "newTeamStyle"
        )
    )
    val editTeamRemarksInputs = listOf(
        InputTextField(
            header = InputHeader(
                title = "新团队介绍"
            ),
            key = "newTeamRemarks"
        )
    )

    LaunchedEffect(Unit) {
        model.refresh()
    }

    InputDialog(
        state = editTeamNameUseCase,
        selection = InputSelection(
            input = editTeamNameInputs,
            onPositiveClick = { inputResult ->
                val newName = inputResult.getString("newTeamName")
                if (newName != null) {
                    scope.launch(Dispatchers.IO) {
                        val result = CFQTeamServer.adminUpdateTeamName(
                            authToken = model.token,
                            game = model.mode,
                            teamId = state.team.info.id,
                            newName = newName
                        )
                        if (result.isEmpty()) {
                            snackbarHostState.showSnackbar("团队名称已更新")
                            model.refresh()
                        } else {
                            snackbarHostState.showSnackbar("团队名称更新失败：$result")
                        }
                    }
                }
            }
        )
    )

    InputDialog(
        state = editTeamStyleUseCase,
        selection = InputSelection(
            input = editTeamStyleInputs,
            onPositiveClick = { inputResult ->
                val newStyle = inputResult.getString("newTeamStyle")
                if (newStyle!= null) {
                    scope.launch(Dispatchers.IO) {
                        val result = CFQTeamServer.adminUpdateTeamStyle(
                            authToken = model.token,
                            game = model.mode,
                            teamId = state.team.info.id,
                            newStyle = newStyle
                        )
                        if (result) {
                            snackbarHostState.showSnackbar("团队方针已更新")
                            model.refresh()
                        } else {
                            snackbarHostState.showSnackbar("团队方针更新失败，请联系开发者")
                        }
                    }
                }
            }
        )
    )

    InputDialog(
        state = editTeamRemarksUseCase,
        selection = InputSelection(
            input = editTeamRemarksInputs,
            onPositiveClick = { inputResult ->
                val newRemarks = inputResult.getString("newTeamRemarks")
                if (newRemarks!= null) {
                    scope.launch(Dispatchers.IO) {
                        val result = CFQTeamServer.adminUpdateTeamRemarks(
                            authToken = model.token,
                            game = model.mode,
                            teamId = state.team.info.id,
                            newRemarks = newRemarks
                        )
                        if (result) {
                            snackbarHostState.showSnackbar("团队介绍已更新")
                            model.refresh()
                        } else {
                            snackbarHostState.showSnackbar("团队介绍更新失败，请联系开发者")
                        }
                    }
                }
            }
        )
    )

    InfoDialog(
        state = confirmRefreshTeamCodeUseCase,
        header = Header.Default(
            title = "刷新团队代码",
            icon = IconSource(imageVector = Icons.Default.Refresh)
        ),
        body = InfoBody.Default(
            bodyText = "确定要刷新团队代码吗？",
        ),
        selection = InfoSelection(
            onPositiveClick = {
                scope.launch(Dispatchers.IO) {
                    val result = CFQTeamServer.adminRotateTeamCode(
                        authToken = model.token,
                        game = model.mode,
                        teamId = state.team.info.id
                    )

                    if (result) {
                        snackbarHostState.showSnackbar("团队代码已刷新")
                        model.refresh()
                    } else {
                        snackbarHostState.showSnackbar("团队代码刷新失败，请联系开发者")
                    }
                }
            }
        )
    )


    InfoDialog(
        state = confirmDeleteTeamUseCase,
        header = Header.Default(
            title = "解散团队",
            icon = IconSource(imageVector = Icons.Default.DeleteForever)
        ),
        body = InfoBody.Default(
            bodyText = "确定要解散该团队吗？",
        ),
        selection = InfoSelection(
            onPositiveClick = {
                scope.launch(Dispatchers.IO) {
                    val result = CFQTeamServer.adminDisbandTeam(
                        authToken = model.token,
                        game = model.mode,
                        teamId = state.team.info.id
                    )
                    if (result) {
                        navController.navigateUp()
                    } else {
                        snackbarHostState.showSnackbar("解散团队失败，请联系开发者")
                    }
                }
            }
        )
    )

    PreferenceSectionHeader(
        title = { Text("基本信息") }
    )
    PreferenceButton(
        title = { Text("团队名称") },
        subtitle = { Text(state.team.info.displayName) },
        icon = { Icon(Icons.Default.Badge, contentDescription = "团队名称") },
        onClick = { editTeamNameUseCase.show() }
    )
    PreferenceButton(
        title = { Text("团队方针") },
        subtitle = { Text(state.team.info.style) },
        icon = { Icon(Icons.Default.Flag, contentDescription = "团队方针") },
        onClick = { editTeamStyleUseCase.show() }
    )
    PreferenceButton(
        title = { Text("团队介绍") },
        subtitle = { Text(state.team.info.remarks) },
        icon = { Icon(Icons.Default.Info, contentDescription = "团队介绍") },
        onClick = { editTeamRemarksUseCase.show() }
    )
    PreferenceBool(
        value = state.team.info.promotable,
        onValueChange = {
            scope.launch(Dispatchers.IO) {
                val result = CFQTeamServer.adminUpdateTeamPromotable(
                    authToken = model.token,
                    game = model.mode,
                    teamId = state.team.info.id,
                    promotable = it
                )
                if (result) {
                    model.refresh()
                    snackbarHostState.showSnackbar("团队状态已更新")
                } else {
                    snackbarHostState.showSnackbar("团队状态更新失败，请联系开发者")
                }
            }
        },
        title = { Text("可被搜索") },
        subtitle = { Text("启用该选项将可以让团队被搜索或推荐") },
        icon = { Icon(Icons.Default.Search, contentDescription = "可被搜索") }
    )

    PreferenceButton(
        title = { Text("管理组曲") },
        subtitle = {
            Text(if (state.team.info.courseName.isEmpty()) "当前未设置组曲" else state.team.info.courseName)
        },
        icon = { Icon(Icons.Default.Ballot, contentDescription = "添加组曲") },
        onClick = { navController.navigate(HomeNavItem.Home.route + "/team/settings/course") }
    )

    PreferenceDivider()

    PreferenceSectionHeader(
        title = { Text("成员管理") }
    )

    PreferenceButton(
        title = { Text("管理成员") },
        subtitle = { Text("当前人数：${state.team.members.size}") },
        icon = { Icon(Icons.Default.Groups, contentDescription = "团队成员") },
        onClick = { navController.navigate(HomeNavItem.Home.route + "/team/settings/member") }
    )

    PreferenceButton(
        title = { Text("管理待加入成员") },
        subtitle = { Text(if (state.team.pendingMembers.isEmpty()) "暂未收到加入申请" else "${state.team.pendingMembers.size}人待加入") },
        icon = { Icon(Icons.Default.PersonAdd, contentDescription = "添加成员") },
        onClick = { navController.navigate(HomeNavItem.Home.route + "/team/settings/pending") }
    )

    PreferenceDivider()

    PreferenceSectionHeader(
        title = { Text("高级功能") }
    )
    PreferenceButton(
        title = { Text("重新生成团队代码") },
        subtitle = { Text("重新生成后将无法通过原有的团队代码搜索到本团队") },
        icon = { Icon(Icons.Default.Refresh, contentDescription = "重新生成团队代码") },
        onClick = { confirmRefreshTeamCodeUseCase.show() }
    )
    PreferenceButton(
        title = { Text("解散团队", color = MaterialTheme.colorScheme.error) },
        subtitle = { Text("解散后将无法撤销") },
        icon = { Icon(Icons.Default.DeleteForever, contentDescription = "解散团队") },
        onClick = { confirmDeleteTeamUseCase.show() },
    )
}
