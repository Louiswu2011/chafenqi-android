package com.nltv.chafenqi.view.home.rating

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.nltv.chafenqi.SCREEN_PADDING
import com.nltv.chafenqi.extension.cutForRating
import com.nltv.chafenqi.extension.rating
import com.nltv.chafenqi.extension.toChunithmCoverPath
import com.nltv.chafenqi.extension.toMaimaiCoverPath
import com.nltv.chafenqi.extension.toRateString
import com.nltv.chafenqi.storage.datastore.user.chunithm.ChunithmRatingEntry
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiBestScoreEntry
import com.nltv.chafenqi.view.module.RatingBadge
import com.nltv.chafenqi.view.songlist.chunithmDifficultyColors
import com.nltv.chafenqi.view.songlist.maimaiDifficultyColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRatingPage(navController: NavController) {
    val model = viewModel<HomeRatingPageViewModel>().also { it.update() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Rating列表") },
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
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            if (model.mode == 0) {
                HomeRatingChunithmList(navController)
            } else if (model.mode == 1) {
                HomeRatingMaimaiList(navController)
            }
        }
    }
}

@Composable
fun HomeRatingMaimaiList(navController: NavController) {
    val model = viewModel<HomeRatingPageViewModel>()

    val listState = rememberLazyListState()
    var pastExpanded by remember {
        mutableStateOf(true)
    }
    var newExpanded by remember {
        mutableStateOf(true)
    }

    Box {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(SCREEN_PADDING),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(SCREEN_PADDING)
        ) {
            item {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(text = model.maiRating, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    Text(text = "Past ${model.maiPastRating} / New ${model.maiNewRating}")
                }
            }
            item {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "旧曲 B35", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(text = if (pastExpanded) "收起" else "展开", Modifier.clickable {
                        pastExpanded = !pastExpanded
                    }, color = MaterialTheme.colorScheme.primary)
                }
            }
            if (pastExpanded) {
                items(
                    count = model.maiPastList.size,
                    key = { index ->
                        model.maiPastList[index].idx + model.maiPastList[index].levelIndex + "P" + index
                    },
                    itemContent = { index ->
                        HomeRatingMaimaiEntry(
                            entry = model.maiPastList[index],
                            index = index,
                            navController = navController
                        )
                    }
                )
            }
            item {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "新曲 B15", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(text = if (newExpanded) "收起" else "展开", Modifier.clickable {
                        newExpanded = !newExpanded
                    }, color = MaterialTheme.colorScheme.primary)
                }
            }
            if (newExpanded) {
                items(
                    count = model.maiNewList.size,
                    key = { index ->
                        model.maiNewList[index].idx + model.maiNewList[index].levelIndex + "N" + index
                    },
                    itemContent = { index ->
                        HomeRatingMaimaiEntry(
                            entry = model.maiNewList[index],
                            index = index,
                            navController = navController
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun HomeRatingMaimaiEntry(entry: MaimaiBestScoreEntry, index: Int, navController: NavController) {
    val model = viewModel<HomeRatingPageViewModel>()

    Row(
        Modifier
            .fillMaxWidth()
            .clickable { model.navigateToMusicEntry(entry, navController) },
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        AsyncImage(
            model = entry.associatedMusicEntry.musicID.toMaimaiCoverPath(),
            contentDescription = "歌曲封面",
            modifier = Modifier
                .size(64.dp)
                .border(
                    border = BorderStroke(
                        width = 4.dp,
                        color = maimaiDifficultyColors[entry.levelIndex]
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(2.dp)
                .clip(RoundedCornerShape(size = 8.dp))
        )
        Column(
            Modifier
                .fillMaxWidth()
                .height(64.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Text(text = "#${index + 1}", Modifier.width(40.dp))
                    Text(
                        text = "${
                            String.format(
                                "%.1f",
                                entry.associatedMusicEntry.constants[entry.levelIndex]
                            )
                        }/${entry.rating()}", fontWeight = FontWeight.Bold
                    )
                }
                RatingBadge(entry.rateString)
            }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = entry.title,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier.fillMaxWidth(fraction = 0.6f)
                )
                Text(
                    text = "${String.format("%.4f", entry.achievements)}%",
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }
}

@Composable
fun HomeRatingChunithmList(navController: NavController) {
    val model = viewModel<HomeRatingPageViewModel>()

    val listState = rememberLazyListState()
    var bestExpanded by remember {
        mutableStateOf(true)
    }
    var recentExpanded by remember {
        mutableStateOf(true)
    }

    Box {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(SCREEN_PADDING),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(SCREEN_PADDING)
        ) {
            item {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(text = model.chuRating, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    Text(text = "Best ${model.chuBestRating} / Recent ${model.chuRecentRating}")
                }
            }
            item {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "最佳成绩 B30", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(text = if (bestExpanded) "收起" else "展开", Modifier.clickable {
                        bestExpanded = !bestExpanded
                    }, color = MaterialTheme.colorScheme.primary)
                }
            }
            if (bestExpanded) {
                items(
                    count = model.chuBestList.size,
                    key = { index ->
                        model.chuBestList[index].idx + model.chuBestList[index].levelIndex + "B" + index
                    },
                    itemContent = { index ->
                        HomeRatingChunithmEntry(
                            entry = model.chuBestList[index],
                            index = index,
                            navController = navController
                        )
                    }
                )
            }
            item {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "最近成绩 R10", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(text = if (recentExpanded) "收起" else "展开", Modifier.clickable {
                        recentExpanded = !recentExpanded
                    }, color = MaterialTheme.colorScheme.primary)
                }
            }
            if (recentExpanded) {
                items(
                    count = model.chuRecentList.size,
                    key = { index ->
                        model.chuRecentList[index].idx + model.chuRecentList[index].levelIndex + "R" + index
                    },
                    itemContent = { index ->
                        HomeRatingChunithmEntry(
                            entry = model.chuRecentList[index],
                            index = index,
                            navController = navController
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun HomeRatingChunithmEntry(entry: ChunithmRatingEntry, index: Int, navController: NavController) {
    val model = viewModel<HomeRatingPageViewModel>()

    Row(
        Modifier
            .fillMaxWidth()
            .clickable { model.navigateToMusicEntry(entry, navController) },
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        AsyncImage(
            model = entry.associatedMusicEntry.musicID.toChunithmCoverPath(),
            contentDescription = "歌曲封面",
            modifier = Modifier
                .size(64.dp)
                .border(
                    border = BorderStroke(
                        width = 4.dp,
                        color = chunithmDifficultyColors[entry.levelIndex]
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(2.dp)
                .clip(RoundedCornerShape(size = 8.dp))
        )
        Column(
            Modifier
                .fillMaxWidth()
                .height(64.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Text(text = "#${index + 1}", Modifier.width(40.dp))
                    Text(
                        text = "${
                            String.format(
                                "%.1f",
                                entry.associatedMusicEntry.charts.constants[entry.levelIndex]
                            )
                        }/${String.format("%.2f", entry.rating().cutForRating())}",
                        fontWeight = FontWeight.Bold
                    )
                }
                RatingBadge(rate = entry.score.toRateString())
            }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = entry.title,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier.fillMaxWidth(fraction = 0.6f)
                )
                Text(text = entry.score.toString(), maxLines = 1, softWrap = false)
            }
        }
    }
}