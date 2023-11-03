package com.nltv.chafenqi.view.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.nltv.chafenqi.storage.CFQUser
import com.nltv.chafenqi.storage.datastore.user.RecentScoreEntry
import com.nltv.chafenqi.storage.datastore.user.chunithm.ChunithmRecentScoreEntry
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiRecentScoreEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class HomePageUiState(
    val mode: Int = 0,

    val nickname: String = "",
    val rating: String = "",
    val playCount: String = "",
    val canNavigateToRecentList: Boolean = true,
    val canNavigateToRatingList: Boolean = true,

    val maiRecentLineup: List<MaimaiRecentScoreEntry> = listOf(),
    val maiPastRating: String = "",
    val maiNewRating: String = "",

    val chuRecentLineup: List<ChunithmRecentScoreEntry> = listOf(),
    val chuMaxRating: String = "",
    val chuBestRating: String = "",
    val chuRecentRating: String = "",

    val recentLineup: List<RecentScoreEntry> = if (mode == 0) chuRecentLineup else maiRecentLineup
)

class HomePageViewModel(

): ViewModel() {
    val tag = "HomePageViewModel"
    val user = CFQUser

    private val _uiState = MutableStateFlow(HomePageUiState())
    val uiState: StateFlow<HomePageUiState> = _uiState.asStateFlow()

    fun update() {
        // TODO: Add recent lineup picking strategy
        Log.i(tag, "Updating home data for game ${user.mode}...")
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
                        maiRecentLineup = user.maimai.recent.take(3),
                        maiPastRating = user.maimai.aux.pastRating.toString(),
                        maiNewRating = user.maimai.aux.newRating.toString(),
                    )
                }
                0 -> {
                    currentState.copy(
                        mode = 0,
                        nickname = user.chunithm.info.nickname,
                        rating = String.format("%.2f", user.chunithm.info.rating),
                        playCount = user.chunithm.info.playCount.toString(),
                        canNavigateToRecentList = user.chunithm.recent.isNotEmpty(),
                        canNavigateToRatingList = user.chunithm.rating.isNotEmpty(),
                        chuRecentLineup = user.chunithm.recent.take(3),
                        chuMaxRating = String.format("%.2f", user.chunithm.info.maxRating),
                        chuBestRating = String.format("%.2f", user.chunithm.aux.bestRating),
                        chuRecentRating = String.format("%.2f", user.chunithm.aux.recentRating)
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

    companion object {
        private const val TIMEOUT_MILLS = 5_000L
    }
}
