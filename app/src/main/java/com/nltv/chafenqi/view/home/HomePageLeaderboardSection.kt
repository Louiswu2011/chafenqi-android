package com.nltv.chafenqi.view.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nltv.chafenqi.data.rank.ChunithmFirstRank
import com.nltv.chafenqi.data.rank.ChunithmRatingRank
import com.nltv.chafenqi.data.rank.ChunithmTotalPlayedRank
import com.nltv.chafenqi.data.rank.ChunithmTotalScoreRank
import com.nltv.chafenqi.data.rank.MaimaiFirstRank
import com.nltv.chafenqi.data.rank.MaimaiRatingRank
import com.nltv.chafenqi.data.rank.MaimaiTotalPlayedRank
import com.nltv.chafenqi.data.rank.MaimaiTotalScoreRank
import java.util.Locale

@Composable
fun HomePageLeaderboardSection(navController: NavController) {
    val model: HomePageViewModel = viewModel()
    val uiState by model.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
    ) {
        HomePageLeaderboardBar(navController)
        HomePageLeaderboardInfo()
    }
}

@Composable
fun HomePageLeaderboardInfo() {
    val model: HomePageViewModel = viewModel()
    val uiState by model.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(model.user.mode) {
        model.fetchLeaderboardRanks()
    }

    AnimatedContent(targetState = uiState.mode, label = "HomePageLeaderboardInfo") {
        when (it) {
            0 -> HomePageChunithmLeaderboardInfo()
            1 -> HomePageMaimaiLeaderboardInfo()
        }
    }
}

@Composable
fun HomePageChunithmLeaderboardInfo() {
    val model: HomePageViewModel = viewModel()
    val uiState by model.uiState.collectAsStateWithLifecycle()

    AnimatedContent(targetState = uiState.isLoadingLeaderboardBar, label = "ChunithmLeaderboardRankInfo") {
        when (it) {
            true -> {
                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator()
                }
            }
            false -> {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 5.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start
                    ) {
                        if (uiState.chuLeaderboardRank[0] is ChunithmRatingRank && uiState.chuLeaderboardRank[0] != null) {
                            val rank = uiState.chuLeaderboardRank[0] as ChunithmRatingRank
                            Text(text = String.format(locale = Locale.ENGLISH, format = "%.2f", rank.rating))
                            Text(text = "#${rank.rank}", fontSize = 14.sp)
                            Text(text = "Rating", fontSize = 12.sp)
                        }
                    }
                    Column(
                        horizontalAlignment = Alignment.Start
                    )  {
                        if (uiState.chuLeaderboardRank[1] is ChunithmTotalScoreRank && uiState.chuLeaderboardRank[1] != null) {
                            val rank = uiState.chuLeaderboardRank[1] as ChunithmTotalScoreRank
                            Text(text = "${rank.totalScore}")
                            Text(text = "#${rank.rank}", fontSize = 14.sp)
                            Text(text = "总分", fontSize = 12.sp)
                        }
                    }
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        if (uiState.chuLeaderboardRank[2] is ChunithmTotalPlayedRank && uiState.chuLeaderboardRank[2] != null) {
                            val rank = uiState.chuLeaderboardRank[2] as ChunithmTotalPlayedRank
                            Text(text = "${rank.totalPlayed}")
                            Text(text = "#${rank.rank}", fontSize = 14.sp)
                            Text(text = "游玩曲目数", fontSize = 12.sp)
                        }
                    }
                    Column(
                        horizontalAlignment = Alignment.End
                    )  {
                        if (uiState.chuLeaderboardRank[3] is ChunithmFirstRank && uiState.chuLeaderboardRank[3] != null) {
                            val rank = uiState.chuLeaderboardRank[3] as ChunithmFirstRank
                            Text(text = "${rank.firstCount}")
                            Text(text = "#${rank.rank}", fontSize = 14.sp)
                            Text(text = "榜一取得数", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomePageMaimaiLeaderboardInfo() {
    val model: HomePageViewModel = viewModel()
    val uiState by model.uiState.collectAsStateWithLifecycle()

    AnimatedContent(targetState = uiState.isLoadingLeaderboardBar, label = "MaimaiLeaderboardRankInfo") {
        when (it) {
            true -> {
                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator()
                }
            }
            false -> {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 5.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start
                    ) {
                        if (uiState.maiLeaderboardRank[0] is MaimaiRatingRank && uiState.maiLeaderboardRank[0] != null) {
                            val rank = uiState.maiLeaderboardRank[0] as MaimaiRatingRank
                            Text(text = "${rank.rating}")
                            Text(text = "#${rank.rank}", fontSize = 14.sp)
                            Text(text = "Rating", fontSize = 12.sp)
                        }
                    }
                    Column(
                        horizontalAlignment = Alignment.Start
                    ) {
                        if (uiState.maiLeaderboardRank[1] is MaimaiTotalScoreRank && uiState.maiLeaderboardRank[1] != null) {
                            val rank = uiState.maiLeaderboardRank[1] as MaimaiTotalScoreRank
                            Text(text = "${String.format(Locale.ENGLISH, "%.4f", rank.totalAchievements)}%")
                            Text(text = "#${rank.rank}", fontSize = 14.sp)
                            Text(text = "总分", fontSize = 12.sp)
                        }
                    }
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        if (uiState.maiLeaderboardRank[2] is MaimaiTotalPlayedRank && uiState.maiLeaderboardRank[2] != null) {
                            val rank = uiState.maiLeaderboardRank[2] as MaimaiTotalPlayedRank
                            Text(text = "${rank.totalPlayed}")
                            Text(text = "#${rank.rank}", fontSize = 14.sp)
                            Text(text = "游玩曲目数", fontSize = 12.sp)
                        }
                    }
                    Column(
                        horizontalAlignment = Alignment.End
                    )  {
                        if (uiState.maiLeaderboardRank[3] is MaimaiFirstRank && uiState.maiLeaderboardRank[3] != null) {
                            val rank = uiState.maiLeaderboardRank[3] as MaimaiFirstRank
                            Text(text = "${rank.firstCount}")
                            Text(text = "#${rank.rank}", fontSize = 14.sp)
                            Text(text = "榜一取得数", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomePageLeaderboardBar(navController: NavController) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "排行榜",
            fontWeight = FontWeight.Bold,
            fontSize = TextUnit(16f, TextUnitType.Sp)
        )
        Text(
            text = "显示全部",
            fontSize = TextUnit(14f, TextUnitType.Sp),
            modifier = Modifier.clickable {
                navController.navigate(HomeNavItem.Home.route + "/leaderboard")
            },
            color = MaterialTheme.colorScheme.primary
        )
    }
}