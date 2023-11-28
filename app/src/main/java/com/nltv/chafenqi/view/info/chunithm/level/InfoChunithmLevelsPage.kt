package com.nltv.chafenqi.view.info.chunithm.level

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.nltv.chafenqi.SCREEN_PADDING
import com.nltv.chafenqi.extension.LEVEL_STRINGS
import com.nltv.chafenqi.extension.RATE_COLORS_CHUNITHM
import com.nltv.chafenqi.extension.RATE_COLORS_MAIMAI
import com.nltv.chafenqi.extension.RATE_STRINGS_CHUNITHM
import com.nltv.chafenqi.extension.RATE_STRINGS_MAIMAI
import com.nltv.chafenqi.extension.rating
import com.nltv.chafenqi.extension.toChunithmCoverPath
import com.nltv.chafenqi.extension.toMaimaiCoverPath
import com.nltv.chafenqi.extension.toRateString
import com.nltv.chafenqi.storage.SettingsStore
import com.nltv.chafenqi.storage.datastore.user.chunithm.ChunithmBestScoreEntry
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiBestScoreEntry
import com.nltv.chafenqi.storage.songlist.chunithm.ChunithmMusicEntry
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiMusicEntry
import com.nltv.chafenqi.util.navigateToMusicEntry
import com.nltv.chafenqi.view.info.maimai.level.InfoLevelLegend
import com.nltv.chafenqi.view.info.maimai.level.InfoMaimaiLevelEntry
import com.nltv.chafenqi.view.info.maimai.level.InfoMaimaiLevelList
import com.nltv.chafenqi.view.info.maimai.level.InfoMaimaiLevelsIndicator
import com.nltv.chafenqi.view.info.maimai.level.InfoMaimaiLevelsLegends
import com.nltv.chafenqi.view.info.maimai.level.InfoMaimaiLevelsViewModel
import com.nltv.chafenqi.view.module.RatingBadge
import com.nltv.chafenqi.view.songlist.chunithmDifficultyColors
import com.nltv.chafenqi.view.songlist.maimaiDifficultyColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoChunithmLevelsPage(navController: NavController) {
// TODO: Add song sort (by score, by play time...)
    val model: InfoChunithmLevelsViewModel = viewModel()
    val uiState by model.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val store = SettingsStore(context)
    val chuDefaultLevelIndex by store.infoLevelsChunithmDefaultLevel.collectAsStateWithLifecycle(initialValue = 18)

    LaunchedEffect(Unit) {
        if (!model.isLoaded) {
            Log.i("ChunithmLevels", "Default level index is $chuDefaultLevelIndex")
            model.assignCurrentPosition(chuDefaultLevelIndex)
            model.isLoaded = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "歌曲完成度") },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回上一级"
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        Column(
            Modifier.padding(paddingValues)
        ) {
            Row (
                Modifier
                    .padding(SCREEN_PADDING)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { model.decreaseLevel() },
                    enabled = model.currentPosition > 0
                ) {
                    Icon(imageVector = Icons.Default.HorizontalRule, contentDescription = "降低等级")
                }
                Text(
                    text = LEVEL_STRINGS[model.currentPosition],
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = { model.increaseLevel() },
                    enabled = model.currentPosition < 22
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "增加等级")
                }
            }
            AnimatedVisibility(visible = uiState.rateSizes.isNotEmpty()) {
                Column {
                    InfoChunithmLevelsIndicator()
                    InfoChunithmLevelsLegends()
                }
            }
            InfoChunithmLevelList(navController)
        }
    }
}

@Composable
fun InfoChunithmLevelsIndicator() {
    val model: InfoChunithmLevelsViewModel = viewModel()
    val uiState by model.uiState.collectAsStateWithLifecycle()

    BoxWithConstraints {
        val mWidth = maxWidth
        Row (
            Modifier
                .padding(SCREEN_PADDING)
                .fillMaxWidth()
                .height(25.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(6.dp)
                )
                .padding(1.dp)
                .clip(RoundedCornerShape(5.dp)),
            horizontalArrangement = Arrangement.Start
        ) {
            // Played
            RATE_STRINGS_CHUNITHM.forEachIndexed { index, _ ->
                val size = uiState.rateSizes[index]
                Spacer(
                    modifier = Modifier
                        .background(RATE_COLORS_CHUNITHM[index], RectangleShape)
                        .animateContentSize()
                        .fillMaxHeight()
                        .width(size.toFloat() / uiState.entrySize * mWidth)

                )
            }
            // Not played yet
            val size = uiState.musicEntries.size
            Spacer(
                modifier = Modifier
                    .background(Color.LightGray, RectangleShape)
                    .animateContentSize()
                    .fillMaxHeight()
                    .width(size.toFloat() / uiState.entrySize * mWidth)
            )
        }
    }
}

@Composable
fun InfoChunithmLevelsLegends() {
    val model: InfoChunithmLevelsViewModel = viewModel()
    val uiState by model.uiState.collectAsStateWithLifecycle()

    Row (
        Modifier
            .padding(SCREEN_PADDING)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RATE_STRINGS_CHUNITHM.forEachIndexed { index, string ->
            InfoLevelLegend(color = RATE_COLORS_MAIMAI[index], description = string, count = uiState.rateSizes[index])
        }
        InfoLevelLegend(color = Color.LightGray, description = "未游玩", count = uiState.rateSizes.last())
    }
}

@Composable
fun InfoChunithmLevelList(navController: NavController) {
    val model: InfoChunithmLevelsViewModel = viewModel()
    val uiState by model.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        Modifier
            .padding(SCREEN_PADDING)
    ) {
        items (
            count = uiState.levelEntries.size,
            key = { index -> uiState.levelEntries[index].title + index }
        ) { index ->
            val entry = uiState.levelEntries[index]
            InfoChunithmLevelEntry(music = entry.associatedMusicEntry, best = entry, navController = navController)
        }
        if (uiState.musicEntries.isNotEmpty()) {
            items(
                count = uiState.musicEntries.size,
                key = { index -> uiState.musicEntries[index].musicID }
            ) {index ->
                val entry = uiState.musicEntries[index]
                InfoChunithmLevelEntry(music = entry, best = null, navController = navController)
            }
        }
    }
}

@Composable
fun InfoChunithmLevelEntry(music: ChunithmMusicEntry, best: ChunithmBestScoreEntry?, navController: NavController) {
    val model: InfoChunithmLevelsViewModel = viewModel()

    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable {
                navigateToMusicEntry(music, navController)
            },
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        AsyncImage(
            model = music.musicID.toChunithmCoverPath(),
            contentDescription = "歌曲封面",
            modifier = Modifier
                .size(64.dp)
                .border(
                    border = BorderStroke(
                        width = 2.dp,
                        color = if (best != null) chunithmDifficultyColors[best.levelIndex] else MaterialTheme.colorScheme.outline
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
                val index = music.charts.levels.indexOf(LEVEL_STRINGS[model.currentPosition])
                Text(
                    text = String.format("%.1f", music.charts.constants[index]) +
                            if (best != null) "/${String.format("%.2f", best.rating())}" else ""
                )
                if (best != null) {
                    RatingBadge(best.score.toRateString())
                }
            }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = music.title,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier.fillMaxWidth(fraction = 0.7f)
                )
                if (best != null) {
                    Text(
                        text = best.score.toString(),
                        maxLines = 1,
                        softWrap = false,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(text = "尚未游玩")
                }
            }
        }
    }
}