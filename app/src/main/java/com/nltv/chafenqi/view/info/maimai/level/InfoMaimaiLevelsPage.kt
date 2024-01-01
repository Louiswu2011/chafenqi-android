package com.nltv.chafenqi.view.info.maimai.level

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
import com.nltv.chafenqi.extension.MAIMAI_LEVEL_STRINGS
import com.nltv.chafenqi.extension.RATE_COLORS_MAIMAI
import com.nltv.chafenqi.extension.RATE_STRINGS_MAIMAI
import com.nltv.chafenqi.extension.rating
import com.nltv.chafenqi.extension.toMaimaiCoverPath
import com.nltv.chafenqi.storage.SettingsStore
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiBestScoreEntry
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiMusicEntry
import com.nltv.chafenqi.util.navigateToMusicEntry
import com.nltv.chafenqi.view.module.RatingBadge
import com.nltv.chafenqi.view.songlist.maimaiDifficultyColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoMaimaiLevelsPage(navController: NavController) {
    // TODO: Add song sort (by score, by play time...)
    val model: InfoMaimaiLevelsViewModel = viewModel()
    val uiState by model.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val store = SettingsStore(context)
    val maiDefaultLevelIndex by store.infoLevelsMaimaiDefaultLevel.collectAsStateWithLifecycle(initialValue = 18)

    LaunchedEffect(Unit) {
        if (!model.isLoaded) {
            Log.i("MaimaiLevels", "Default level index is $maiDefaultLevelIndex")
            model.assignCurrentPosition(maiDefaultLevelIndex)
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
                    text = MAIMAI_LEVEL_STRINGS[model.currentPosition],
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
                    InfoMaimaiLevelsIndicator()
                    InfoMaimaiLevelsLegends()
                }
            }
            InfoMaimaiLevelList(navController)
        }
    }
}

@Composable
fun InfoMaimaiLevelsIndicator() {
    val model: InfoMaimaiLevelsViewModel = viewModel()
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
            RATE_STRINGS_MAIMAI.forEachIndexed { index, _ ->
                val size = uiState.rateSizes[index]
                Spacer(
                    modifier = Modifier
                        .background(RATE_COLORS_MAIMAI[index], RectangleShape)
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
fun InfoMaimaiLevelsLegends() {
    val model: InfoMaimaiLevelsViewModel = viewModel()
    val uiState by model.uiState.collectAsStateWithLifecycle()
    
    Row (
        Modifier
            .padding(SCREEN_PADDING)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RATE_STRINGS_MAIMAI.forEachIndexed { index, string ->
            InfoLevelLegend(color = RATE_COLORS_MAIMAI[index], description = string, count = uiState.rateSizes[index])
        }
        InfoLevelLegend(color = Color.LightGray, description = "未游玩", count = uiState.rateSizes.last())
    }
}

@Composable
fun InfoLevelLegend(color: Color, description: String, count: Int) {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(
            modifier = Modifier
                .background(color, RoundedCornerShape(2.dp))
                .height(5.dp)
                .width(20.dp)
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 5.dp)
        )
        Text(
            text = count.toString(),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun InfoMaimaiLevelList(navController: NavController) {
    val model: InfoMaimaiLevelsViewModel = viewModel()
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
            InfoMaimaiLevelEntry(music = entry.associatedMusicEntry, best = entry, navController = navController)
        }
        if (uiState.musicEntries.isNotEmpty()) {
            items(
                count = uiState.musicEntries.size,
                key = { index -> uiState.musicEntries[index].musicID }
            ) {index ->
                val entry = uiState.musicEntries[index]
                InfoMaimaiLevelEntry(music = entry, best = null, navController = navController)
            }
        }
    }
}

@Composable
fun InfoMaimaiLevelEntry(music: MaimaiMusicEntry, best: MaimaiBestScoreEntry?, navController: NavController) {
    val model: InfoMaimaiLevelsViewModel = viewModel()

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
            model = music.musicID.toMaimaiCoverPath(),
            contentDescription = "歌曲封面",
            modifier = Modifier
                .size(64.dp)
                .border(
                    border = BorderStroke(
                        width = 2.dp,
                        color = if (best != null) maimaiDifficultyColors[best.levelIndex] else MaterialTheme.colorScheme.outline
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
                val index = music.level.indexOf(MAIMAI_LEVEL_STRINGS[model.currentPosition])
                val constant = if (index != -1) music.constants[index] else 0.0
                Text(text = String.format("%.1f", constant) +
                        if (best != null) "/${best.rating()}" else ""
                )
                if (best != null) {
                    RatingBadge(best.rateString)
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
                        text = "${String.format("%.4f", best.achievements)}%",
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