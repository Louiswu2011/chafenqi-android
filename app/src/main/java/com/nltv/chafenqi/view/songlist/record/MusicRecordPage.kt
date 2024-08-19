package com.nltv.chafenqi.view.songlist.record

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.nltv.chafenqi.SCREEN_PADDING
import com.nltv.chafenqi.extension.toChunithmCoverPath
import com.nltv.chafenqi.extension.toDateString
import com.nltv.chafenqi.extension.toMaimaiCoverPath
import com.nltv.chafenqi.extension.toRateString
import com.nltv.chafenqi.storage.datastore.user.chunithm.ChunithmRecentScoreEntry
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiRecentScoreEntry
import com.nltv.chafenqi.storage.user.CFQUser
import com.nltv.chafenqi.util.ChunithmAxisValueOverrider
import com.nltv.chafenqi.util.MaimaiAxisValueOverrider
import com.nltv.chafenqi.util.navigateToRecentEntry
import com.nltv.chafenqi.view.home.HomeNavItem
import com.nltv.chafenqi.view.module.RatingBadge
import com.nltv.chafenqi.view.songlist.chunithmDifficultyColors
import com.nltv.chafenqi.view.songlist.maimaiDifficultyColors
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.AxisValueOverrider
import java.util.Locale

@Composable
fun MusicRecordPage(navController: NavController, mode: Int, index: Int, levelIndex: Int) {
    val model = viewModel<MusicRecordPageViewModel>().also {
        it.update(mode, index, levelIndex)
    }
    val uiState by model.uiState.collectAsStateWithLifecycle()

    if (mode == 0 && uiState.chuHistoryEntries.isEmpty() || mode == 1 && uiState.maiHistoryEntries.isEmpty()) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "这里空荡荡的，要不出勤打这首？")
        }
    } else if (!CFQUser.isPremium) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "订阅会员以查询游玩记录")
            TextButton(onClick = { navController.navigate(HomeNavItem.Home.route + "/settings/user/redeem") }) {
                Row {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                        contentDescription = "订阅会员"
                    )
                    Spacer(modifier = Modifier.padding(ButtonDefaults.IconSpacing))
                    Text(text = "了解详情")
                }
            }
        }
    } else {
        Column(
            Modifier
                .fillMaxWidth()
        ) {
            MusicRecordScoreChart()
            if (mode == 0) {
                MusicRecordChunithmEntryList(navController)
            } else if (mode == 1) {
                MusicRecordMaimaiEntryList(navController)
            }
        }
    }
}

@Composable
fun MusicRecordScoreChart() {
    val model: MusicRecordPageViewModel = viewModel()
    val uiState by model.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberVicoScrollState()

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                axisValueOverrider = if (model.mode == 0) ChunithmAxisValueOverrider() else MaimaiAxisValueOverrider(),
            ),
            startAxis = rememberStartAxis(
                valueFormatter = { value, _, _ ->
                    if (model.mode == 0) {
                        String.format(Locale.getDefault(), "%.0f", value)
                    } else {
                        String.format(Locale.getDefault(), "%.4f", value) + "%"
                    }
                },
                itemPlacer = VerticalAxis.ItemPlacer.count( { 10 } )
            ),
            bottomAxis = rememberBottomAxis(
                valueFormatter = { value, _, _ ->
                    if (model.mode == 0) {
                        uiState.chuHistoryDateStringMap[value.toInt()] ?: ""
                    } else {
                        uiState.maiHistoryDateStringMap[value.toInt()] ?: ""
                    }
                }
            )
        ),
        modelProducer = if (model.mode == 0) model.chuEntryProvider else model.maiEntryProvider,
        scrollState = scrollState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(SCREEN_PADDING))
}

@Composable
fun MusicRecordMaimaiEntryList(navController: NavController) {
    val model: MusicRecordPageViewModel = viewModel()
    val uiState by model.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LazyColumn(
        Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SCREEN_PADDING)
    ) {
        items(
            count = uiState.maiHistoryEntries.size,
            key = { index ->
                uiState.maiHistoryEntries[index].timestamp
            }
        ) { index ->
            val entry = uiState.maiHistoryEntries[index]
            MusicRecordEntry(
                modifier = Modifier.padding(horizontal = SCREEN_PADDING),
                mode = 1,
                coverUrl = entry.associatedMusicEntry.musicID.toMaimaiCoverPath(),
                title = entry.title,
                levelIndex = entry.levelIndex,
                playDate = entry.timestamp.toDateString(context),
                badge = entry.achievements.toRateString(),
                score = String.format("%.4f", entry.achievements) + "%",
                navController = navController,
                maiRecentEntry = entry
            )
        }
    }
}

@Composable
fun MusicRecordChunithmEntryList(navController: NavController) {
    val model: MusicRecordPageViewModel = viewModel()
    val uiState by model.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LazyColumn(
        Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SCREEN_PADDING)
    ) {
        items(
            count = uiState.chuHistoryEntries.size,
            key = { index ->
                uiState.chuHistoryEntries[index].timestamp
            }
        ) { index ->
            val entry = uiState.chuHistoryEntries[index]
            MusicRecordEntry(
                modifier = Modifier.padding(horizontal = SCREEN_PADDING),
                mode = 0,
                coverUrl = entry.associatedMusicEntry.musicID.toChunithmCoverPath(),
                title = entry.title,
                levelIndex = entry.levelIndex,
                playDate = entry.timestamp.toDateString(context),
                badge = entry.score.toRateString(),
                score = entry.score.toString(),
                navController = navController,
                chuRecentEntry = entry
            )
        }
    }
}

@Composable
fun MusicRecordEntry(
    modifier: Modifier = Modifier,
    mode: Int,
    coverUrl: String,
    title: String,
    levelIndex: Int,
    playDate: String,
    badge: String,
    score: String,
    navController: NavController? = null,
    maiRecentEntry: MaimaiRecentScoreEntry? = null,
    chuRecentEntry: ChunithmRecentScoreEntry? = null
) {
    Row(
        modifier = Modifier
            .then(modifier)
            .fillMaxWidth()
            .height(64.dp)
            .clickable(enabled = navController != null) {
                if (navController == null) return@clickable
                if (maiRecentEntry != null) {
                    navigateToRecentEntry(maiRecentEntry, navController)
                    return@clickable
                }
                if (chuRecentEntry != null) {
                    navigateToRecentEntry(chuRecentEntry, navController)
                    return@clickable
                }
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AsyncImage(
            model = coverUrl,
            contentDescription = "歌曲封面",
            modifier = Modifier
                .padding(end = 8.dp)
                .size(64.dp)
                .border(
                    border = BorderStroke(
                        width = 3.dp,
                        color = if (mode == 0) chunithmDifficultyColors[levelIndex] else maimaiDifficultyColors[levelIndex]
                    ), shape = RoundedCornerShape(8.dp)
                )
                .padding(2.dp)
                .clip(RoundedCornerShape(size = 8.dp))
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = playDate)
                RatingBadge(rate = badge)
            }
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth(0.6f)
                )
                Text(
                    text = score,
                    maxLines = 1
                )
            }
        }
    }
}