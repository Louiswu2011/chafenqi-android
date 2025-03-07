package com.nltv.chafenqi.view.home

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.nltv.chafenqi.extension.toChunithmCoverPath
import com.nltv.chafenqi.extension.toDateString
import com.nltv.chafenqi.extension.toMaimaiCoverPath
import com.nltv.chafenqi.storage.user.ChunithmRecentLineup
import com.nltv.chafenqi.storage.user.MaimaiRecentLineup
import com.nltv.chafenqi.view.songlist.chunithmDifficultyColors
import com.nltv.chafenqi.view.songlist.maimaiDifficultyColors

@Composable
fun HomePageRecentSection(navController: NavController) {
    Column {
        HomePageRecentBar(navController = navController)
        HomePageRecentLineup(navController = navController)
    }
}

@Composable
fun HomePageRecentBar(navController: NavController) {
    val model: HomePageViewModel = viewModel()
    val uiState by model.uiState.collectAsState()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .padding(bottom = 10.dp)
    ) {
        Text(
            text = "最近动态",
            fontWeight = FontWeight.Bold,
            fontSize = TextUnit(16f, TextUnitType.Sp)
        )
        Text(
            text = "显示全部",
            fontSize = TextUnit(14f, TextUnitType.Sp),
            modifier = Modifier.clickable(enabled = uiState.canNavigateToRecentList) {
                model.navigateToRecentList(navController)
            },
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun HomePageRecentLineup(navController: NavController) {
    val model: HomePageViewModel = viewModel()
    val uiState by model.uiState.collectAsState()

    Box {
        Crossfade(targetState = uiState.mode, label = "home recent entries crossfade") {
            when (it) {
                0 -> {
                    Column(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (uiState.chuRecentLineup.isEmpty()) {
                            Text(text = "暂无数据", modifier = Modifier.fillMaxWidth())
                        } else {
                            uiState.chuRecentLineup.onEach { entry ->
                                Column(
                                    Modifier.clickable {
                                        model.navigateToRecentLog(navController, entry)
                                    }
                                ) {
                                    HomePageRecentChunithmEntry(entry)
                                }
                            }
                        }
                    }
                }

                1 -> {
                    Column(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (uiState.maiRecentLineup.isEmpty()) {
                            Text(text = "暂无数据", modifier = Modifier.fillMaxWidth())
                        } else {
                            uiState.maiRecentLineup.onEach { entry ->
                                Column(
                                    Modifier.clickable {
                                        model.navigateToRecentLog(navController, entry)
                                    }
                                ) {
                                    HomePageRecentMaimaiEntry(entry)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomePageRecentMaimaiEntry(item: MaimaiRecentLineup) {
    val context = LocalContext.current

    Row(
        Modifier
            .fillMaxWidth()
            .height(72.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AsyncImage(
            model = item.entry.associatedMusicEntry.coverId.toMaimaiCoverPath(),
            contentDescription = "最近动态歌曲封面",
            modifier = Modifier
                .padding(end = 8.dp)
                .size(72.dp)
                .border(
                    border = BorderStroke(
                        width = 2.dp,
                        color = maimaiDifficultyColors[item.entry.levelIndex]
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                // .padding(2.dp)
                .clip(RoundedCornerShape(size = 10.dp))
        )
        Column(
            Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                Modifier.fillMaxWidth(),
                Arrangement.SpaceBetween
            ) {
                Text(item.entry.timestamp.toDateString(context), fontSize = 14.sp)
                Text(text = item.tag, fontWeight = FontWeight.Bold)
            }
            Row(
                Modifier.fillMaxWidth(),
                Arrangement.SpaceBetween,
                Alignment.CenterVertically
            ) {
                Text(
                    item.entry.associatedMusicEntry.title,
                    fontSize = 16.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth(0.6f)
                )
                Text(
                    text = "%.4f".format(item.entry.achievements).plus("%"),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun HomePageRecentChunithmEntry(item: ChunithmRecentLineup) {
    val context = LocalContext.current

    Row(
        Modifier
            .fillMaxWidth()
            .height(72.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AsyncImage(
            model = item.entry.associatedMusicEntry.musicId.toChunithmCoverPath(),
            contentDescription = "最近动态歌曲封面",
            modifier = Modifier
                .padding(end = 8.dp)
                .size(72.dp)
                .border(
                    border = BorderStroke(
                        width = 2.dp,
                        color = chunithmDifficultyColors[item.entry.levelIndex]
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(2.dp)
                .clip(RoundedCornerShape(size = 10.dp))
        )
        Column(
            Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                Modifier.fillMaxWidth(),
                Arrangement.SpaceBetween
            ) {
                Text(item.entry.timestamp.toDateString(context), fontSize = 14.sp)
                Text(text = item.tag, fontWeight = FontWeight.Bold)
            }
            Row(
                Modifier.fillMaxWidth(),
                Arrangement.SpaceBetween,
                Alignment.CenterVertically
            ) {
                Text(
                    item.entry.associatedMusicEntry.title,
                    fontSize = 16.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth(0.6f)
                )
                Text(
                    text = item.entry.score.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}