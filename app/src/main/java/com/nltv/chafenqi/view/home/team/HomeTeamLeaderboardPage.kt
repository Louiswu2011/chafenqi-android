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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
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

@OptIn(ExperimentalMaterial3Api::class)
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
                    IconButton(onClick = {
                        model.refresh()
                    }) {
                        Icon(Icons.Default.Refresh, "刷新排行榜")
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