package com.nltv.chafenqi.view.home.team.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Ballot
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.michaelflisar.composepreferences.core.PreferenceDivider
import com.michaelflisar.composepreferences.core.PreferenceInfo
import com.michaelflisar.composepreferences.core.PreferenceScreen
import com.michaelflisar.composepreferences.core.PreferenceSectionHeader
import com.michaelflisar.composepreferences.core.hierarchy.PreferenceRootScope
import com.michaelflisar.composepreferences.screen.button.PreferenceButton
import com.michaelflisar.composepreferences.screen.list.PreferenceList
import com.nltv.chafenqi.view.home.team.HomeTeamPageViewModel
import com.nltv.chafenqi.view.home.team.settings.TeamSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTeamSettingsCoursePage(navController: NavController) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold (
        topBar = {
            LargeTopAppBar(
                title = { Text("组曲设置") },
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
            TeamCourseSettings(
                navController = navController,
                snackbarHostState = snackbarHostState
            )
        }
    }
}

@Composable
fun PreferenceRootScope.TeamCourseSettings(
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {
    val model: HomeTeamPageViewModel = viewModel()
    val state by model.uiState.collectAsStateWithLifecycle()

    var life by remember { mutableIntStateOf(state.team.info.courseHealth) }

    PreferenceSectionHeader(
        title = { Text("基本信息") }
    )

    PreferenceButton(
        title = { Text("组曲名称") },
        subtitle = { Text(if (state.team.info.courseName.isEmpty()) "暂未设置" else state.team.info.courseName) },
        icon = { Icon(Icons.Default.Ballot, contentDescription = "组曲名称") },
        onClick = { /* TODO: Navigate to add course page */ }
    )

    PreferenceList(
        style = PreferenceList.Style.Spinner,
        value = life,
        onValueChange = {},
        items = listOf(1, 10, 50, 100, 200, 0),
        itemTextProvider = {
            if (it == 0) "无限制" else "$it"
        },
        title = { Text("生命值") },
        subtitle = { Text(if (state.team.info.courseTrack1 == null) "暂未设置" else "${state.team.info.courseHealth}") },
        icon = { Icon(Icons.Default.HeartBroken, contentDescription = "生命值") }
    )

    PreferenceDivider()

    PreferenceSectionHeader(
        title = { Text("歌曲配置") }
    )

    state.team.info.courseTracks.forEachIndexed { index, track ->
        PreferenceButton(
            title = { Text("TRACK ${index + 1}") },
            subtitle = {
                Text(
                    buildAnnotatedString {
                        if (track == null) {
                            append("暂未设定")
                        } else {
                            withStyle(style = SpanStyle(color = Color(model.getDifficultyColor(track)))) {
                                append(model.getTitle(track))
                            }
                        }
                    }
                )
            },
            onClick = { /* TODO: Navigate to add course page */ }
        )
    }

    PreferenceDivider()

    PreferenceButton(
        title = { Text("更新组曲") },
        subtitle = { Text("组曲更新后将会重置当前排行榜") },
        icon = { Icon(Icons.Default.Refresh, contentDescription = "更新组曲") },
        onClick = {  }
    )

    PreferenceInfo(
        title = { Text("组曲每30天只能修改一次") },
        icon = { Icon(Icons.Default.Info, contentDescription = "组曲相关信息") }
    )
}