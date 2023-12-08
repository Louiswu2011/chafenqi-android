package com.nltv.chafenqi.view.home

import android.view.MotionEvent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.nltv.chafenqi.SCREEN_PADDING
import com.nltv.chafenqi.extension.rating
import com.nltv.chafenqi.extension.toChunithmCoverPath
import com.nltv.chafenqi.extension.toMaimaiCoverPath
import com.nltv.chafenqi.extension.toRateString
import com.nltv.chafenqi.util.getChunithmCardGradientStop
import com.nltv.chafenqi.util.getMaimaiCardGradientStop
import com.nltv.chafenqi.view.module.RatingBadge
import com.nltv.chafenqi.view.songlist.chunithmDifficultyColors
import com.nltv.chafenqi.view.songlist.maimaiDifficultyColors

@Composable
fun HomePageRatingBar(navController: NavController) {
    val model: HomePageViewModel = viewModel()
    val uiState by model.uiState.collectAsState()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {
        Text(
            text = "Rating分析",
            fontWeight = FontWeight.Bold,
            fontSize = TextUnit(16f, TextUnitType.Sp)
        )
        Text(
            text = "显示全部",
            fontSize = TextUnit(14f, TextUnitType.Sp),
            modifier = Modifier.clickable(enabled = uiState.canNavigateToRatingList) {
                navController.navigate(HomeNavItem.Home.route + "/rating")
            },
            color = MaterialTheme.colorScheme.primary
        )
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomePageRatingIndicators() {
    val density = LocalDensity.current
    val model = viewModel<HomePageViewModel>()
    val uiState by model.uiState.collectAsState()

    var isTouching by remember { mutableStateOf(false) }
    var touchPoint by remember { mutableStateOf(Offset.Zero) }
    var canvasWidth by remember { mutableStateOf(Size.Zero) }

    fun updatePointer(event: MotionEvent, maxWidth: Dp) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchPoint = Offset(event.x, event.y)
                isTouching = true
            }

            MotionEvent.ACTION_MOVE -> {
                touchPoint = Offset(event.x, event.y)
                isTouching = true
            }

            MotionEvent.ACTION_UP -> {
                touchPoint = Offset(event.x, event.y)
                isTouching = false
                model.resetRatingIndicators()
            }
        }
        if (isTouching) {
            model.updateRatingIndicators(touchPoint, maxWidth, density)
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Crossfade(targetState = uiState.mode, label = "rating indicators crossfade") { mode ->
            when (mode) {
                0 -> {
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(SCREEN_PADDING)
                            .height(10.dp)
                            .pointerInteropFilter {
                                with(density) {
                                    updatePointer(it, canvasWidth.toDpSize().width)
                                }
                                true
                            }
                    ) {
                        canvasWidth = size
                        val gridWidth = size.width / (uiState.chuIndicatorsCount * 2 - 1).toFloat()
                        repeat(uiState.chuIndicatorsCount) { index ->
                            drawRect(
                                getChunithmCardGradientStop(1 - index / uiState.chuIndicatorsCount.toFloat()),
                                topLeft = Offset(
                                    if (index != 0) (gridWidth * 2) * index else 0f,
                                    0f
                                ),
                                size = Size(
                                    gridWidth,
                                    model.getIndicatorHeight(
                                        index,
                                        uiState.currentSelectedIndicatorIndex,
                                        density,
                                        isTouching
                                    )
                                )
                            )
                        }
                    }
                }

                1 -> {
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(SCREEN_PADDING)
                            .height(10.dp)
                            .pointerInteropFilter {
                                with(density) {
                                    updatePointer(it, canvasWidth.toDpSize().width)
                                }
                                true
                            }
                    ) {
                        canvasWidth = size
                        val gridWidth = size.width / (uiState.maiIndicatorsCount * 2 - 1).toFloat()
                        repeat(uiState.maiIndicatorsCount) { index ->
                            drawRect(
                                getMaimaiCardGradientStop(1 - index / uiState.maiIndicatorsCount.toFloat()),
                                topLeft = Offset(
                                    if (index != 0) (gridWidth * 2) * index else 0f,
                                    0f
                                ),
                                size = Size(
                                    gridWidth,
                                    model.getIndicatorHeight(
                                        index,
                                        uiState.currentSelectedIndicatorIndex,
                                        density,
                                        isTouching
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomePageRatingSelection(navController: NavController) {
    val model = viewModel<HomePageViewModel>()
    val uiState by model.uiState.collectAsState()


    Crossfade(
        targetState = uiState.mode,
        label = "home page rating selection crossfade",
        modifier = Modifier.padding(
            horizontal = SCREEN_PADDING
        )
    ) {
        when (it) {
            0 -> {
                HomePageChunithmRatingSelection(navController)
            }

            1 -> {
                HomePageMaimaiRatingSelection(navController)
            }
        }
    }
}

@Composable
fun HomePageMaimaiRatingSelection(navController: NavController) {
    val model = viewModel<HomePageViewModel>()
    val uiState by model.uiState.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable {
                model.navigateToMusicEntry(uiState.maiCurrentSelectedRatingEntry, navController)
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AsyncImage(
            model = uiState.maiCurrentSelectedRatingEntry.associatedMusicEntry.musicID.toMaimaiCoverPath(),
            contentDescription = "歌曲封面",
            modifier = Modifier
                .padding(end = 8.dp)
                .size(64.dp)
                .border(
                    border = BorderStroke(
                        width = 3.dp,
                        color = maimaiDifficultyColors[uiState.maiCurrentSelectedRatingEntry.levelIndex]
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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(SCREEN_PADDING),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(text = "${uiState.maiCurrentSelectedRatingEntryType} #${uiState.maiCurrentSelectedRatingEntryRank}")
                    Text(
                        text = "${
                            String.format(
                                "%.1f",
                                uiState.maiCurrentSelectedRatingEntry.associatedMusicEntry.constants[uiState.maiCurrentSelectedRatingEntry.levelIndex]
                            )
                        }/${uiState.maiCurrentSelectedRatingEntry.rating()}",
                        fontWeight = FontWeight.Bold
                    )
                }
                RatingBadge(rate = uiState.maiCurrentSelectedRatingEntry.rateString)
            }
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = uiState.maiCurrentSelectedRatingEntry.title,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth(0.6f)
                )
                Text(
                    text = String.format(
                        "%.4f",
                        uiState.maiCurrentSelectedRatingEntry.achievements
                    ) + "%",
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun HomePageChunithmRatingSelection(navController: NavController) {
    val model = viewModel<HomePageViewModel>()
    val uiState by model.uiState.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable {
                model.navigateToMusicEntry(uiState.chuCurrentSelectedRatingEntry, navController)
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AsyncImage(
            model = uiState.chuCurrentSelectedRatingEntry.associatedMusicEntry.musicID.toChunithmCoverPath(),
            contentDescription = "歌曲封面",
            modifier = Modifier
                .padding(end = 8.dp)
                .size(64.dp)
                .border(
                    border = BorderStroke(
                        width = 3.dp,
                        color = chunithmDifficultyColors[uiState.chuCurrentSelectedRatingEntry.levelIndex]
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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(SCREEN_PADDING)
                ) {
                    Text(text = "#${uiState.currentSelectedIndicatorIndex + 1}")
                    Text(
                        text = "${
                            String.format(
                                "%.1f",
                                uiState.chuCurrentSelectedRatingEntry.associatedMusicEntry.charts.constants[uiState.chuCurrentSelectedRatingEntry.levelIndex]
                            )
                        }/${String.format("%.2f", uiState.chuCurrentSelectedRatingEntry.rating())}",
                        fontWeight = FontWeight.Bold
                    )
                }
                RatingBadge(rate = uiState.chuCurrentSelectedRatingEntry.score.toRateString())
            }
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = uiState.chuCurrentSelectedRatingEntry.title,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth(0.6f)
                )
                Text(
                    text = uiState.chuCurrentSelectedRatingEntry.score.toString(),
                    maxLines = 1
                )
            }
        }
    }
}