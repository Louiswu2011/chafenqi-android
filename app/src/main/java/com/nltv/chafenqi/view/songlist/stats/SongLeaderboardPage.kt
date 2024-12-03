package com.nltv.chafenqi.view.songlist.stats

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nltv.chafenqi.data.leaderboard.ChunithmDiffLeaderboard
import com.nltv.chafenqi.data.leaderboard.ChunithmDiffLeaderboardItem
import com.nltv.chafenqi.data.leaderboard.MaimaiDiffLeaderboard
import com.nltv.chafenqi.data.leaderboard.MaimaiDiffLeaderboardItem
import com.nltv.chafenqi.extension.RATE_STRINGS_CHUNITHM
import com.nltv.chafenqi.storage.user.CFQUser
import com.nltv.chafenqi.view.module.RatingBadge
import java.util.Locale

@Composable
fun ChunithmLeaderboardPage(index: Int, difficulty: Int) {
    val model = viewModel<SongStatsPageViewModel>()
    val state by model.statsState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        model.fetchLeaderboard(mode = 0, index = index, difficulty = difficulty)
    }

    AnimatedContent(targetState = state.doneLoadingLeaderboard, label = "Load Animation") {
        when (it) {
            true -> {
                if (state.chunithmDiffLeaderboard.isNotEmpty()) {
                    ChunithmLeaderboardColumn(
                        leaderboard = state.chunithmDiffLeaderboard
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "哦不，还没有人游玩过该难度！")
                    }
                }
            }

            false -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun ChunithmLeaderboardColumn(leaderboard: ChunithmDiffLeaderboard) {
    val listState = rememberLazyListState()
    val userEntry = leaderboard.find { it.username == CFQUser.username }

    Box {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(horizontal = 10.dp)
        ) {
            items(
                count = leaderboard.size,
                key = { index -> leaderboard[index].id },
                itemContent = { index ->
                    val item = leaderboard[index]
                    ChunithmLeaderboardRow(index = index, item = item)
                }
            )
        }

        if (userEntry != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(MaterialTheme.colorScheme.surface)
                    .drawBehind {
                        val borderSize = 1.dp
                        val borderColor = Color.Black

                        drawLine(
                            color = borderColor,
                            start = Offset(0f, 0f),
                            end = Offset(size.width, 0f),
                            strokeWidth = borderSize.toPx()
                        )
                    }
                    .padding(5.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                ChunithmLeaderboardRow(
                    index = leaderboard.indexOf(userEntry),
                    item = userEntry,
                    disableHighlight = true
                )
            }
        }
    }

}

@Composable
fun ChunithmLeaderboardRow(
    index: Int,
    item: ChunithmDiffLeaderboardItem,
    disableHighlight: Boolean = false
) {
    Card(
        modifier = Modifier.padding(vertical = 5.dp),
        shape = RoundedCornerShape(5.dp),
        border = if (item.username == CFQUser.username && !disableHighlight) BorderStroke(
            2.dp,
            MaterialTheme.colorScheme.primary
        ) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#${index + 1}",
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.padding(horizontal = 5.dp))
                Text(text = item.nickname)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (item.rankIndex > 8) {
                    RatingBadge(rate = RATE_STRINGS_CHUNITHM[13 - item.rankIndex])
                }

                Spacer(modifier = Modifier.padding(horizontal = 5.dp))
                Text(
                    text = item.score.toString(),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun MaimaiLeaderboardPage(index: Int, difficulty: Int, type: String) {
    val model = viewModel<SongStatsPageViewModel>()
    val state by model.statsState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        model.fetchLeaderboard(mode = 1, index = index, difficulty = difficulty, type = type)
    }

    AnimatedContent(targetState = state.doneLoadingLeaderboard, label = "Load Animation") {
        when (it) {
            true -> {
                if (state.maimaiDiffLeaderboard.isNotEmpty()) {
                    MaimaiLeaderboardColumn(
                        leaderboard = state.maimaiDiffLeaderboard
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "哦不，还没有人游玩过该难度！")
                    }
                }
            }

            false -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun MaimaiLeaderboardColumn(leaderboard: MaimaiDiffLeaderboard) {
    val listState = rememberLazyListState()
    val userEntry = leaderboard.find { it.username == CFQUser.username }

    Box {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(horizontal = 10.dp)
        ) {
            items(
                count = leaderboard.size,
                key = { index -> leaderboard[index].id },
                itemContent = { index ->
                    val item = leaderboard[index]
                    MaimaiLeaderboardRow(index = index, item = item)
                }
            )
        }

        if (userEntry != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(MaterialTheme.colorScheme.surface)
                    .drawBehind {
                        val borderSize = 1.dp
                        val borderColor = Color.Black

                        drawLine(
                            color = borderColor,
                            start = Offset(0f, 0f),
                            end = Offset(size.width, 0f),
                            strokeWidth = borderSize.toPx()
                        )
                    }
                    .padding(5.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                MaimaiLeaderboardRow(
                    index = leaderboard.indexOf(userEntry),
                    item = userEntry,
                    disableHighlight = true
                )
            }
        }
    }

}

@Composable
fun MaimaiLeaderboardRow(
    index: Int,
    item: MaimaiDiffLeaderboardItem,
    disableHighlight: Boolean = false
) {
    Card(
        modifier = Modifier.padding(vertical = 5.dp),
        shape = RoundedCornerShape(5.dp),
        border = if (item.username == CFQUser.username && !disableHighlight) BorderStroke(
            2.dp,
            MaterialTheme.colorScheme.primary
        ) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#${index + 1}",
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.padding(horizontal = 5.dp))
                Text(text = item.nickname)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RatingBadge(rate = item.rate.toRateString())
                Spacer(modifier = Modifier.padding(horizontal = 5.dp))
                Text(
                    text = String.format(Locale.ENGLISH, "%.4f", item.achievements) + "%",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}