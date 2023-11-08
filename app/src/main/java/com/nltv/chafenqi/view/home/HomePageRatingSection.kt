package com.nltv.chafenqi.view.home

import android.util.Log
import android.view.MotionEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nltv.chafenqi.SCREEN_PADDING
import com.nltv.chafenqi.storage.datastore.user.chunithm.ChunithmRatingEntry
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiBestScoreEntry

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
            }
        }
        if (isTouching) {
            model.updateRatingIndicators(touchPoint, maxWidth, density)
        }
    }

    BoxWithConstraints(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Crossfade(targetState = uiState.mode, label = "rating indicators crossfade") { mode ->
            when (mode) {
                0 -> {
                    Row(
                        Modifier
                            .padding(SCREEN_PADDING)
                            .pointerInteropFilter {
                                updatePointer(it, this@BoxWithConstraints.maxWidth)
                                true
                            }
                            .height(40.dp),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        repeat(uiState.chuIndicatorsCount) { index ->
                            Canvas(
                                modifier = Modifier
                                    .padding(start = if (index == 0) 0.dp else this@BoxWithConstraints.maxWidth / (uiState.chuIndicatorsCount * 2 - 1))
                                    .animateContentSize(animationSpec = tween())
                                    .size(
                                        width = this@BoxWithConstraints.maxWidth / (uiState.chuIndicatorsCount * 2 - 1),
                                        height = if (isTouching) model.getIndicatorHeight(index, uiState.currentSelectedIndicatorIndex) else 20.dp
                                    ),
                                onDraw = {
                                    drawRect(color = Color.Magenta)
                                }
                            )
                        }
                    }
                }

                1 -> {
                    Row(
                        Modifier
                            .padding(SCREEN_PADDING)
                            .pointerInteropFilter {
                                updatePointer(it, this@BoxWithConstraints.maxWidth)
                                true
                            }
                            .height(40.dp),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        repeat(uiState.maiIndicatorsCount) { index ->
                            Canvas(
                                modifier = Modifier
                                    .padding(start = if (index == 0) 0.dp else this@BoxWithConstraints.maxWidth / (uiState.maiIndicatorsCount * 2 - 1))
                                    .animateContentSize(animationSpec = tween())
                                    .size(
                                        width = this@BoxWithConstraints.maxWidth / (uiState.maiIndicatorsCount * 2 - 1),
                                        height = if (isTouching) model.getIndicatorHeight(index, uiState.currentSelectedIndicatorIndex) else 20.dp
                                    ),
                                onDraw = {
                                    drawRect(color = Color.Cyan)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomePageMaimaiRatingSelection(entry: MaimaiBestScoreEntry) {

}

@Composable
fun HomePageChunithmRatingSelection(entry: ChunithmRatingEntry) {

}