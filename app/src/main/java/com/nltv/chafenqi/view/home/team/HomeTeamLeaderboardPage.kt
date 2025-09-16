package com.nltv.chafenqi.view.home.team

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nltv.chafenqi.model.team.TeamBasicInfo
import com.nltv.chafenqi.view.home.leaderboard.HomeLeaderboardPageViewModel
import com.nltv.chafenqi.view.module.InfoBlock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.DateTimeFormatBuilder
import kotlinx.datetime.format.Padding
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.YearMonth
import java.util.Calendar
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun HomeTeamLeaderboardPage(navController: NavController) {
    val model: HomeTeamLeaderboardViewModel = viewModel()
    val state by model.uiState.collectAsStateWithLifecycle()

    val currentDate =
        Clock
            .System
            .now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
    val daysInMonth =
        YearMonth.now().lengthOfMonth()

    var expanded by remember { mutableStateOf(false) }
    var showHelpSheet by remember { mutableStateOf(false) }

    val helpSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        model.refresh()
    }

    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "团队排行榜") },
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
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "更多"
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("刷新") },
                            leadingIcon = { Icon(Icons.Default.Refresh, "刷新排行榜") },
                            onClick = {
                                model.refresh()
                            }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("帮助") },
                            leadingIcon = { Icon(Icons.AutoMirrored.Outlined.Help, "帮助") },
                            onClick = {
                                showHelpSheet = true
                            }
                        )
                    }
                }
            )
        },
    ) { paddingValues ->
        Crossfade(state.isLoading) {
            when (it) {
                true -> {
                    Column(
                        modifier =
                            Modifier
                                .padding(paddingValues)
                                .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                false -> {
                    LazyColumn(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Column (
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = currentDate
                                        .format(
                                            LocalDateTime.Format {
                                                year()
                                                chars(" 第")
                                                monthNumber(Padding.NONE)
                                                chars("赛季")
                                            }
                                        ),
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = currentDate
                                        .format(
                                            LocalDateTime.Format {
                                                year()
                                                chars("-")
                                                monthNumber()
                                                chars("-01 ~ ")
                                                year()
                                                chars("-")
                                                monthNumber()
                                                chars("-${daysInMonth}")
                                            }
                                        ),
                                    style = MaterialTheme.typography.bodySmall
                                )
                                HorizontalDivider()
                            }
                        }

                        items(
                            count = state.teams.size,
                            key = { index -> state.teams[index].id }
                        ) { index ->
                            val team = state.teams[index]
                            HomeTeamLeaderboardEntry(team, index)
                        }
                    }
                }
            }
        }

        if (showHelpSheet) {
            ModalBottomSheet(
                onDismissRequest = { showHelpSheet = false },
                sheetState = helpSheetState
            ) {
                HomeTeamLeaderboardHelpSheet { showHelpSheet = false }
            }
        }

    }
}

@Composable
fun HomeTeamLeaderboardEntry(
    team: TeamBasicInfo,
    index: Int
) {
    val indexColor =
        when (index) {
            0 -> Color(red = 175, green = 149, blue = 0)
            1 -> Color(red = 180, green = 180, blue = 180)
            2 -> Color(red = 106, green = 56, blue = 5)
            else -> null
        }

    Column (
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .padding(bottom = 10.dp)
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#${index + 1}",
                color = indexColor ?: Color.Unspecified,
                fontWeight = if (index <= 2) FontWeight.Bold else FontWeight.Normal
            )
            Text(
                text = team.displayName,
                fontWeight = if (index <= 2) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp)
            )
            Text(
                text = if (team.currentActivityPoints > 0) "${team.currentActivityPoints}Pt" else "暂未参加",
                modifier = Modifier.wrapContentWidth()
            )
        }
        Text(
            text = team.remarks,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun HomeTeamLeaderboardHelpSheet(
    onDismissRequest: () -> Unit
) {
    val model = viewModel<HomeTeamLeaderboardViewModel>()

    Column (
        modifier =
        Modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("团队排行榜帮助")
        Column (
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            model.helpData.forEach { data ->
                InfoBlock(
                    icon = data.icon,
                    title = data.title,
                    content = data.content,
                )
            }
        }
        Button(onClick = onDismissRequest) {
            Text("关闭")
        }
    }
}