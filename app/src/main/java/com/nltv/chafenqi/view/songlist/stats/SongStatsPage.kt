package com.nltv.chafenqi.view.songlist.stats

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.nltv.chafenqi.data.ChunithmLeaderboard
import com.nltv.chafenqi.data.ChunithmLeaderboardItem
import com.nltv.chafenqi.data.ChunithmMusicStat
import com.nltv.chafenqi.data.MaimaiLeaderboard
import com.nltv.chafenqi.data.MaimaiLeaderboardItem
import com.nltv.chafenqi.extension.MAIMAI_MISS_JUDGE_TYPE
import com.nltv.chafenqi.extension.MAIMAI_NOTE_TYPE
import com.nltv.chafenqi.extension.RATE_COLORS_CHUNITHM
import com.nltv.chafenqi.extension.RATE_STRINGS_CHUNITHM
import com.nltv.chafenqi.extension.toChunithmCoverPath
import com.nltv.chafenqi.extension.toMaimaiCoverPath
import com.nltv.chafenqi.storage.user.CFQUser
import com.nltv.chafenqi.view.module.RatingBadge
import com.nltv.chafenqi.view.songlist.ChunithmDifficultyInfo
import com.nltv.chafenqi.view.songlist.MaimaiDifficultyInfo
import com.nltv.chafenqi.view.songlist.chunithmDifficultyColors
import com.nltv.chafenqi.view.songlist.maimaiDifficultyColors
import com.nltv.chafenqi.view.songlist.record.MusicRecordPage
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongStatsPage(mode: Int, index: Int, difficulty: Int, navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "排行榜与统计数据") },
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
            modifier = Modifier.padding(paddingValues)
        ) {
            if (mode == 0) {
                ChunithmStatView(musicIndex = index, difficulty = difficulty, navController)
            } else {
                MaimaiStatView(
                    musicIndex = index,
                    difficulty = difficulty,
                    navController = navController
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChunithmStatView(musicIndex: Int, difficulty: Int, navController: NavController) {
    val model =
        viewModel<SongStatsPageViewModel>().also { it.loadSong(mode = 0, index = musicIndex) }
    val state by model.statsState.collectAsStateWithLifecycle()

    var selectedTabIndex by rememberSaveable {
        mutableIntStateOf(0)
    }
    val pagerState = rememberPagerState {
        model.statsTabs.size
    }

    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }

    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            selectedTabIndex = pagerState.currentPage
        }
    }


    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AsyncImage(
                model = state.chunithmMusicEntry.musicID.toChunithmCoverPath(),
                contentDescription = "歌曲封面",
                modifier = Modifier
                    .size(128.dp)
                    .border(
                        border = BorderStroke(
                            width = 3.dp,
                            color = chunithmDifficultyColors[difficulty]
                        ), shape = RoundedCornerShape(12.dp)
                    )
                    .padding(2.dp)
                    .clip(RoundedCornerShape(size = 12.dp))
            )
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = state.chunithmMusicEntry.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = state.chunithmMusicEntry.artist,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        TabRow(selectedTabIndex = selectedTabIndex) {
            model.statsTabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(text = tab.title) },
                    icon = {
                        Icon(
                            imageVector = if (selectedTabIndex == index) tab.selectedIcon else tab.unselectedIcon,
                            contentDescription = tab.title
                        )
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { index ->
            when (index) {
                0 -> {
                    ChunithmLeaderboardPage(index = musicIndex, difficulty = difficulty)
                }

                1 -> {
                    ChunithmMusicStatPage(index = musicIndex, difficulty = difficulty)
                }

                2 -> {
                    MusicRecordPage(
                        navController = navController,
                        mode = 0,
                        index = musicIndex,
                        levelIndex = difficulty
                    )
                }
            }
        }
    }
}

@Composable
fun ChunithmMusicStatPage(index: Int, difficulty: Int) {
    val model = viewModel<SongStatsPageViewModel>()
    val state by model.statsState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        model.fetchStats(mode = 0, index = index, difficulty = difficulty)
    }

    AnimatedContent(targetState = state.doneLoadingStats, label = "loading stats") {
        when (it) {
            true -> {
                if (state.chunithmMusicStat.musicId == 0) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "哦不，还没有人游玩过该难度！")
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ChunithmMusicStatTab(
                            musicStat = state.chunithmMusicStat,
                            info = ChunithmDifficultyInfo(
                                title = state.chunithmMusicEntry.title,
                                difficultyIndex = difficulty,
                                musicEntry = state.chunithmMusicEntry
                            )
                        )
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
fun ChunithmMusicStatTab(musicStat: ChunithmMusicStat, info: ChunithmDifficultyInfo) {
    var lastValue = -90f

    val splitValues = listOf(
        musicStat.ssspSplit,
        musicStat.sssSplit,
        musicStat.sspSplit,
        musicStat.ssSplit,
        musicStat.spSplit,
        musicStat.sSplit,
        musicStat.otherSplit
    )
    val chartValues = splitValues
        .map { (it * 360f / musicStat.totalPlayed) }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .animateContentSize()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .animateContentSize(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "定数：${info.constant}")
            Text(text = "谱师：${info.charter}")
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "总游玩人数：${musicStat.totalPlayed}")
            Text(
                text = "平均分数：${
                    String.format(
                        Locale.ENGLISH,
                        "%.0f",
                        musicStat.totalScore / musicStat.totalPlayed
                    )
                }"
            )
        }

        Row(
            Modifier.height(200.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight()
                    .padding(start = 10.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Canvas(
                    modifier = Modifier.size(140.dp)
                ) {
                    chartValues.forEachIndexed { index, value ->
                        drawArc(
                            color = RATE_COLORS_CHUNITHM[index],
                            startAngle = lastValue,
                            sweepAngle = value,
                            useCenter = false,
                            style = Stroke(35f, cap = StrokeCap.Butt)
                        )

                        lastValue += value
                    }
                }

            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.Start
                ) {
                    splitValues.forEachIndexed { index, split ->
                        Text(
                            buildAnnotatedString {
                                withStyle(
                                    SpanStyle(
                                        fontWeight = FontWeight.Bold,
                                        color = RATE_COLORS_CHUNITHM[index]
                                    )
                                ) {
                                    append(RATE_STRINGS_CHUNITHM[index])
                                }
                                append("：")
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(split.toString())
                                }
                            }
                        )
                    }
                }
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "拟合定数")
                    Text(text = "拟合定数", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.padding(vertical = 10.dp))
                    Text(text = "最高分")
                    Text(
                        text = String.format(Locale.ENGLISH, "%.0f", musicStat.highestScore),
                        fontWeight = FontWeight.Bold
                    )

                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MaimaiStatView(musicIndex: Int, difficulty: Int, navController: NavController) {
    val model =
        viewModel<SongStatsPageViewModel>().also { it.loadSong(mode = 1, index = musicIndex) }
    val state by model.statsState.collectAsStateWithLifecycle()

    var selectedTabIndex by rememberSaveable {
        mutableIntStateOf(0)
    }
    val pagerState = rememberPagerState {
        model.statsTabs.size
    }

    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }

    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            selectedTabIndex = pagerState.currentPage
        }
    }


    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AsyncImage(
                model = state.maimaiMusicEntry.musicID.toMaimaiCoverPath(),
                contentDescription = "歌曲封面",
                modifier = Modifier
                    .size(128.dp)
                    .border(
                        border = BorderStroke(
                            width = 3.dp,
                            color = maimaiDifficultyColors[difficulty]
                        ), shape = RoundedCornerShape(12.dp)
                    )
                    .padding(2.dp)
                    .clip(RoundedCornerShape(size = 12.dp))
            )
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = state.maimaiMusicEntry.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = state.maimaiMusicEntry.basicInfo.artist,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        TabRow(selectedTabIndex = selectedTabIndex) {
            model.statsTabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(text = tab.title) },
                    icon = {
                        Icon(
                            imageVector = if (selectedTabIndex == index) tab.selectedIcon else tab.unselectedIcon,
                            contentDescription = tab.title
                        )
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { index ->
            when (index) {
                0 -> {
                    MaimaiLeaderboardPage(
                        index = musicIndex,
                        difficulty = difficulty,
                        type = state.maimaiMusicEntry.type
                    )
                }

                1 -> {
                    MaimaiStatPage(
                        index = musicIndex,
                        difficulty = difficulty,
                        type = state.maimaiMusicEntry.type,
                        info = MaimaiDifficultyInfo(
                            title = state.maimaiMusicEntry.title,
                            difficultyIndex = difficulty,
                            musicEntry = state.maimaiMusicEntry
                        )
                    )
                }

                2 -> {
                    MusicRecordPage(
                        navController = navController,
                        mode = 1,
                        index = musicIndex,
                        levelIndex = difficulty
                    )
                }
            }
        }
    }
}

@Composable
fun MaimaiStatPage(index: Int, difficulty: Int, type: String, info: MaimaiDifficultyInfo?) {
    val model = viewModel<SongStatsPageViewModel>()
    val state by model.statsState.collectAsStateWithLifecycle()
    val chartEntry = state.maimaiMusicEntry.charts[difficulty]

    LaunchedEffect(Unit) {
        model.fetchStats(mode = 1, index = index, difficulty = difficulty)
    }

    Column(
        modifier = Modifier.padding(10.dp)
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "定数：${info?.constant}")
            Text(text = "谱师：${info?.charter}")
        }

        if (chartEntry.notes.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column (
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(text = "")
                    MAIMAI_NOTE_TYPE.forEachIndexed { index, type ->
                        if (index == 3 && chartEntry.possibleNormalLoss[3].isNotEmpty()) {
                            Text(text = type, fontWeight = FontWeight.Bold)
                        } else if (index != 3) {
                            Text(text = type, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Row (
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    MAIMAI_MISS_JUDGE_TYPE.forEachIndexed { judgeIndex, judgeType ->
                        Column (
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(text = judgeType, fontWeight = FontWeight.Bold)
                            chartEntry.possibleNormalLoss.forEachIndexed { lossIndex, _ ->
                                if (chartEntry.possibleNormalLoss[lossIndex].isNotEmpty() && judgeIndex < chartEntry.possibleNormalLoss.size - 1) {
                                    Text(text = chartEntry.possibleNormalLoss[lossIndex][judgeIndex])
                                }
                            }
                        }
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
                    .padding(bottom = 10.dp)
            ) {
                Text(text = "Break", fontWeight = FontWeight.Bold)

                Row (
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "${chartEntry.possibleBreakLoss[2]}\n${chartEntry.possibleBreakLoss[4]}")
                        // Text(text = chartEntry.possibleBreakLoss[4])
                    }

                    Text(text = chartEntry.possibleBreakLoss[5])
                    Text(text = chartEntry.possibleBreakLoss[6])
                }
            }

            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "50/100落", fontWeight = FontWeight.Bold)
                    Text(text = "${chartEntry.possibleBreakLoss[0]} /")
                    Text(text = chartEntry.possibleBreakLoss[1])
                }

                Column (
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "SSS/SSS+容错", fontWeight = FontWeight.Bold)
                    Text(
                        text = "-${
                            String.format(
                                Locale.ENGLISH,
                                "%.1f",
                                chartEntry.lossUntilSSS
                            )
                        } / -${
                            String.format(
                                Locale.ENGLISH,
                                "%.1f",
                                chartEntry.lossUntilSSSPlus
                            )
                        }"
                    )
                }

                Column (
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "50落/Great比", fontWeight = FontWeight.Bold)
                    Text(
                        text = String.format(
                            Locale.ENGLISH,
                            "%.1f",
                            chartEntry.breakToGreatRatio
                        )
                    )
                }
            }
        }
    }
}


fun String.toRateString(): String {
    return this.replace("p", "+").uppercase()
}

@Composable
@Preview(showBackground = true)
fun SongLeaderboardPagePreview() {
    ChunithmLeaderboardColumn(
        leaderboard = listOf(
            ChunithmLeaderboardItem(
                id = 1,
                uid = 12,
                username = "chafenqi1",
                nickname = "Player1",
                highscore = 1000000,
                rankIndex = 13
            ),
            ChunithmLeaderboardItem(
                id = 2,
                uid = 12,
                username = "chafenqi2",
                nickname = "Player2",
                highscore = 1000000,
                rankIndex = 13
            ),
            ChunithmLeaderboardItem(
                id = 3,
                uid = 12,
                username = "chafenqi3",
                nickname = "Player3",
                highscore = 1000000,
                rankIndex = 13
            ),
            ChunithmLeaderboardItem(
                id = 4,
                uid = 12,
                username = "chafenqi4",
                nickname = "Player4",
                highscore = 1000000,
                rankIndex = 13
            ),
            ChunithmLeaderboardItem(
                id = 5,
                uid = 12,
                username = "chafenqi5",
                nickname = "Player5",
                highscore = 1000000,
                rankIndex = 13
            ),
            ChunithmLeaderboardItem(
                id = 6,
                uid = 12,
                username = "chafenqi6",
                nickname = "Player6",
                highscore = 1000000,
                rankIndex = 13
            )
        )
    )
}