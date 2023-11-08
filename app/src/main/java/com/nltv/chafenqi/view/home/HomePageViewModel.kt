package com.nltv.chafenqi.view.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.nltv.chafenqi.SCREEN_PADDING
import com.nltv.chafenqi.storage.CFQUser
import com.nltv.chafenqi.storage.datastore.user.RecentScoreEntry
import com.nltv.chafenqi.storage.datastore.user.chunithm.ChunithmRatingEntry
import com.nltv.chafenqi.storage.datastore.user.chunithm.ChunithmRecentScoreEntry
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiBestScoreEntry
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiRecentScoreEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.ceil
import kotlin.math.floor

data class HomePageUiState(
    val mode: Int = 0,

    val nickname: String = "",
    val rating: String = "",
    val playCount: String = "",
    val canNavigateToRecentList: Boolean = true,
    val canNavigateToRatingList: Boolean = true,

    val nameplateUpdateTime: String = "",

    val maiRecentLineup: List<MaimaiRecentScoreEntry> = listOf(),
    val maiPastRating: String = "",
    val maiNewRating: String = "",
    val maiIndicatorsCount: Int = 0,
    val maiPastIndicatorsCount: Int = 0,
    val maiNewIndicatorsCount: Int = 0,
    val maiPastRatingList: List<MaimaiBestScoreEntry> = listOf(),
    val maiNewRatingList: List<MaimaiBestScoreEntry> = listOf(),
    val maiCurrentSelectedRatingEntry: MaimaiBestScoreEntry = MaimaiBestScoreEntry(),

    val chuRecentLineup: List<ChunithmRecentScoreEntry> = listOf(),
    val chuMaxRating: String = "",
    val chuBestRating: String = "",
    val chuRecentRating: String = "",
    val chuIndicatorsCount: Int = 0,
    val chuBestRatingList: List<ChunithmRatingEntry> = listOf(),
    val chuCurrentSelectedRatingEntry: ChunithmRatingEntry = ChunithmRatingEntry(),

    val recentLineup: List<RecentScoreEntry> = if (mode == 0) chuRecentLineup else maiRecentLineup,
    val currentSelectedIndicatorIndex: Int = -1,
    val indicatorHeights: MutableList<Dp> = mutableListOf(),
)

class HomePageViewModel(

): ViewModel() {
    val tag = "HomePageViewModel"
    val user = CFQUser

    private val _uiState = MutableStateFlow(HomePageUiState())
    val uiState: StateFlow<HomePageUiState> = _uiState.asStateFlow()

    private val maiIndicatorsCount = user.maimai.aux.pastBest.size + user.maimai.aux.newBest.size
    private val chuIndicatorsCount = user.chunithm.aux.bestList.size

    private var previousIndex = -1

    fun update() {
        // TODO: Add recent lineup picking strategy
        Log.i(tag, "Updating home data for game ${user.mode}...")
        Log.i(tag, user.maimai.info.updatedAt)
        _uiState.update { currentState ->
            when (user.mode) {
                1 -> {
                    currentState.copy(
                        mode = 1,
                        nickname = user.maimai.info.nickname,
                        rating = user.maimai.info.rating.toString(),
                        playCount = user.maimai.info.playCount.toString(),
                        canNavigateToRecentList = user.maimai.recent.isNotEmpty(),
                        canNavigateToRatingList = user.maimai.aux.pastBest.isNotEmpty() && user.maimai.aux.newBest.isNotEmpty(),
                        nameplateUpdateTime = user.maimai.aux.updateTime,
                        maiRecentLineup = user.maimai.recent.take(3),
                        maiPastRating = user.maimai.aux.pastRating.toString(),
                        maiNewRating = user.maimai.aux.newRating.toString(),
                        maiIndicatorsCount = this.maiIndicatorsCount,
                        maiPastIndicatorsCount = user.maimai.aux.pastBest.size,
                        maiNewIndicatorsCount = user.maimai.aux.newBest.size,
                        maiPastRatingList = user.maimai.aux.pastBest,
                        maiNewRatingList = user.maimai.aux.newBest,
                        indicatorHeights = MutableList(this.maiIndicatorsCount) { 20.dp }
                    )
                }
                0 -> {
                    currentState.copy(
                        mode = 0,
                        nickname = user.chunithm.info.nickname,
                        rating = String.format("%.2f", user.chunithm.info.rating),
                        playCount = user.chunithm.info.playCount.toString(),
                        nameplateUpdateTime = user.chunithm.aux.updateTime,
                        canNavigateToRecentList = user.chunithm.recent.isNotEmpty(),
                        canNavigateToRatingList = user.chunithm.rating.isNotEmpty(),
                        chuRecentLineup = user.chunithm.recent.take(3),
                        chuMaxRating = String.format("%.2f", user.chunithm.info.maxRating),
                        chuBestRating = String.format("%.2f", user.chunithm.aux.bestRating),
                        chuRecentRating = String.format("%.2f", user.chunithm.aux.recentRating),
                        chuIndicatorsCount = user.chunithm.aux.bestList.size,
                        chuBestRatingList = user.chunithm.aux.bestList,
                        indicatorHeights = MutableList(this.chuIndicatorsCount) { 20.dp }
                    )
                }
                else -> {
                    currentState
                }
            }
        }
    }

    fun switchGame() {
        Log.i(tag, "Switching game mode from ${user.mode} to ${1 - user.mode}...")
        user.mode = 1 - user.mode
        update()
    }

    fun navigateToRecentList(navController: NavController) {
        if (!_uiState.value.canNavigateToRecentList) return

        navController.navigate(HomeNavItem.Home.route + "/recent")
    }

    fun navigateToRecentLog(navController: NavController, index: Int = -1) {
        if (!_uiState.value.canNavigateToRecentList || index < 0) return

        val navigationKeyword = if (user.mode == 0) "chunithm" else "maimai"
        navController.navigate(HomeNavItem.Home.route + "/recent/$navigationKeyword/$index")
    }

    fun updateRatingIndicators(touchPoint: Offset, maxWidth: Dp, density: Density) {
        with(density) {
            val indicatorCount =
                if (user.mode == 1) _uiState.value.maiIndicatorsCount else _uiState.value.chuIndicatorsCount
            val x = touchPoint.x.toDp()

            val gridWidth = maxWidth / (indicatorCount * 2 - 1)
            val position = ceil(x / gridWidth / 2).toInt() - 1

            if (position != previousIndex) {
                _uiState.update { currentValue ->
                    currentValue.copy(currentSelectedIndicatorIndex = position)
                }
                previousIndex = position
            }
        }
    }

    fun getIndicatorHeight(index: Int, currentPosition: Int): Dp {
        return when (currentPosition) {
            index -> 40.dp
            index - 1, index + 1 -> 25.dp
            index - 2, index + 2 -> 22.dp
            else -> 20.dp
        }
    }

    companion object {
        private const val TIMEOUT_MILLS = 5_000L
    }
}
