package com.nltv.chafenqi.view.home.leaderboard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nltv.chafenqi.data.leaderboard.ChunithmFirstLeaderboard
import com.nltv.chafenqi.data.leaderboard.ChunithmFirstLeaderboardItem
import com.nltv.chafenqi.data.leaderboard.ChunithmRatingLeaderboard
import com.nltv.chafenqi.data.leaderboard.ChunithmRatingLeaderboardItem
import com.nltv.chafenqi.data.leaderboard.ChunithmTotalPlayedLeaderboard
import com.nltv.chafenqi.data.leaderboard.ChunithmTotalPlayedLeaderboardItem
import com.nltv.chafenqi.data.leaderboard.ChunithmTotalScoreLeaderboard
import com.nltv.chafenqi.data.leaderboard.ChunithmTotalScoreLeaderboardItem
import com.nltv.chafenqi.data.leaderboard.MaimaiFirstLeaderboard
import com.nltv.chafenqi.data.leaderboard.MaimaiFirstLeaderboardItem
import com.nltv.chafenqi.data.leaderboard.MaimaiRatingLeaderboard
import com.nltv.chafenqi.data.leaderboard.MaimaiRatingLeaderboardItem
import com.nltv.chafenqi.data.leaderboard.MaimaiTotalPlayedLeaderboard
import com.nltv.chafenqi.data.leaderboard.MaimaiTotalPlayedLeaderboardItem
import com.nltv.chafenqi.data.leaderboard.MaimaiTotalScoreLeaderboard
import com.nltv.chafenqi.data.leaderboard.MaimaiTotalScoreLeaderboardItem
import com.nltv.chafenqi.networking.CFQServer
import com.nltv.chafenqi.storage.user.CFQUser
import com.nltv.chafenqi.view.songlist.stats.SongStatsTabItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

data class HomeLeaderboardPageUiState(
    val doneLoadingMaimaiRatingLeaderboard: Boolean = false,
    val doneLoadingMaimaiTotalScoreLeaderboard: Boolean = false,
    val doneLoadingMaimaiTotalPlayedLeaderboard: Boolean = false,
    val doneLoadingMaimaiFirstLeaderboard: Boolean = false,

    val maimaiRatingLeaderboardRawData: MaimaiRatingLeaderboard = mutableListOf(),
    val maimaiTotalScoreLeaderboardRawData: MaimaiTotalScoreLeaderboard = mutableListOf(),
    val maimaiTotalPlayedLeaderboardRawData: MaimaiTotalPlayedLeaderboard = mutableListOf(),
    val maimaiFirstLeaderboardRawData: MaimaiFirstLeaderboard = mutableListOf(),

    val doneLoadingChunithmRatingLeaderboard: Boolean = false,
    val doneLoadingChunithmTotalScoreLeaderboard: Boolean = false,
    val doneLoadingChunithmTotalPlayedLeaderboard: Boolean = false,
    val doneLoadingChunithmFirstLeaderboard: Boolean = false,

    val chuRatingLeaderboardRawData: ChunithmRatingLeaderboard = mutableListOf(),
    val chuTotalScoreLeaderboardRawData: ChunithmTotalScoreLeaderboard = mutableListOf(),
    val chuTotalPlayedLeaderboardRawData: ChunithmTotalPlayedLeaderboard = mutableListOf(),
    val chuFirstLeaderboardRawData: ChunithmFirstLeaderboard = mutableListOf(),

    val ratingLeaderboard: List<HomeLeaderboardRow> = emptyList(),
    val totalScoreLeaderboard: List<HomeLeaderboardRow> = emptyList(),
    val totalPlayedLeaderboard: List<HomeLeaderboardRow> = emptyList(),
    val firstLeaderboard: List<HomeLeaderboardRow> = emptyList()
)

data class HomeLeaderboardRow(
    val index: Int,
    val uid: String,
    val username: String,
    val nickname: String,
    val info: String,
    val extraInfo: Any? = null
)

class HomeLeaderboardPageViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(HomeLeaderboardPageUiState())
    val uiState = _uiState.asStateFlow()

    val mode = CFQUser.mode
    val token = CFQUser.token

    val statsTabs = listOf(
        SongStatsTabItem(
            title = "Rating",
            unselectedIcon = Icons.Outlined.Leaderboard,
            selectedIcon = Icons.Filled.Leaderboard
        ),
        SongStatsTabItem(
            title = "总分",
            unselectedIcon = Icons.Outlined.PieChart,
            selectedIcon = Icons.Filled.PieChart
        ),
        SongStatsTabItem(
            title = "游玩曲目数",
            unselectedIcon = Icons.AutoMirrored.Outlined.List,
            selectedIcon = Icons.AutoMirrored.Filled.List
        ),
        SongStatsTabItem(
            title = "榜一取得数",
            unselectedIcon = Icons.Outlined.WorkspacePremium,
            selectedIcon = Icons.Filled.WorkspacePremium
        )
    )

    fun update() {
        when (mode) {
            0 -> {
                updateChunithm()
            }
            1 -> {
                updateMaimai()
            }
        }
    }

    private fun updateChunithm() {
        viewModelScope.launch {
            if (!_uiState.value.doneLoadingChunithmRatingLeaderboard) {
                _uiState.update {
                    it.copy(
                        doneLoadingChunithmRatingLeaderboard = true,
                        chuRatingLeaderboardRawData = CFQServer.apiTotalLeaderboard<ChunithmRatingLeaderboardItem>(
                            authToken = token,
                            gameType = 0
                        )
                    )
                }
            }
            _uiState.update {
                it.copy(
                    ratingLeaderboard = it.chuRatingLeaderboardRawData.mapIndexed { index, item ->
                        HomeLeaderboardRow(
                            index = index,
                            uid = item.uid.toString(),
                            username = item.username,
                            nickname = item.nickname,
                            info = String.format(Locale.ENGLISH, "%.2f", item.rating)
                        )
                    }
                )
            }
        }
        viewModelScope.launch {
            if (!_uiState.value.doneLoadingChunithmTotalScoreLeaderboard) {
                _uiState.update {
                    it.copy(
                        doneLoadingChunithmTotalScoreLeaderboard = true,
                        chuTotalScoreLeaderboardRawData = CFQServer.apiTotalLeaderboard<ChunithmTotalScoreLeaderboardItem>(
                            authToken = token,
                            gameType = 0
                        )
                    )
                }
            }
            _uiState.update {
                it.copy(
                    totalScoreLeaderboard = it.chuTotalScoreLeaderboardRawData.mapIndexed { index, item ->
                        HomeLeaderboardRow(
                            index = index,
                            uid = item.uid.toString(),
                            username = item.username,
                            nickname = item.nickname,
                            info = item.totalScore.toString()
                        )
                    }
                )
            }
        }
        viewModelScope.launch {
            if (!_uiState.value.doneLoadingChunithmTotalPlayedLeaderboard) {
                _uiState.update {
                    it.copy(
                        doneLoadingChunithmTotalPlayedLeaderboard = true,
                        chuTotalPlayedLeaderboardRawData = CFQServer.apiTotalLeaderboard<ChunithmTotalPlayedLeaderboardItem>(
                            authToken = token,
                            gameType = 0
                        )
                    )
                }
            }
            _uiState.update {
                it.copy(
                    totalPlayedLeaderboard = it.chuTotalPlayedLeaderboardRawData.mapIndexed { index, item ->
                        HomeLeaderboardRow(
                            index = index,
                            uid = item.uid.toString(),
                            username = item.username,
                            nickname = item.nickname,
                            info = item.totalPlayed.toString()
                        )
                    }
                )
            }
        }
        viewModelScope.launch {
            if (!_uiState.value.doneLoadingChunithmFirstLeaderboard) {
                _uiState.update {
                    it.copy(
                        doneLoadingChunithmFirstLeaderboard = true,
                        chuFirstLeaderboardRawData = CFQServer.apiTotalLeaderboard<ChunithmFirstLeaderboardItem>(
                            authToken = token,
                            gameType = 0
                        )
                    )
                }
            }
            _uiState.update {
                it.copy(
                    firstLeaderboard = it.chuFirstLeaderboardRawData.mapIndexed { index, item ->
                        HomeLeaderboardRow(
                            index = index,
                            uid = item.uid.toString(),
                            username = item.username,
                            nickname = item.nickname,
                            info = item.firstCount.toString(),
                            extraInfo = item.firstMusics
                        )
                    }
                )
            }
        }
    }

    private fun updateMaimai() {
        viewModelScope.launch {
            if(!_uiState.value.doneLoadingMaimaiRatingLeaderboard) {
                _uiState.update {
                    it.copy(
                        doneLoadingMaimaiRatingLeaderboard = true,
                        maimaiRatingLeaderboardRawData = CFQServer.apiTotalLeaderboard<MaimaiRatingLeaderboardItem>(
                            authToken = token,
                            gameType = 1
                            )
                    )
                }
            }
            _uiState.update {
                it.copy(
                    ratingLeaderboard = it.maimaiRatingLeaderboardRawData.mapIndexed { index, item ->
                        HomeLeaderboardRow(
                            index = index,
                            uid = item.uid.toString(),
                            username = item.username,
                            nickname = item.nickname,
                            info = item.rating.toString()
                        )
                    }
                )
            }
        }
        viewModelScope.launch {
            if(!_uiState.value.doneLoadingMaimaiTotalScoreLeaderboard) {
                _uiState.update {
                    it.copy(
                        doneLoadingMaimaiTotalScoreLeaderboard = true,
                        maimaiTotalScoreLeaderboardRawData = CFQServer.apiTotalLeaderboard<MaimaiTotalScoreLeaderboardItem>(
                            authToken = token,
                            gameType = 1
                        )
                    )
                }
            }
            _uiState.update {
                it.copy(
                    totalScoreLeaderboard = it.maimaiTotalScoreLeaderboardRawData.mapIndexed { index, item ->
                        HomeLeaderboardRow(
                            index = index,
                            uid = item.uid.toString(),
                            username = item.username,
                            nickname = item.nickname,
                            info = String.format(Locale.ENGLISH, "%.4f", item.totalAchievements) + "%"
                        )
                    }
                )
            }
        }
        viewModelScope.launch {
            if(!_uiState.value.doneLoadingMaimaiTotalPlayedLeaderboard) {
                _uiState.update {
                    it.copy(
                        doneLoadingMaimaiTotalPlayedLeaderboard = true,
                        maimaiTotalPlayedLeaderboardRawData = CFQServer.apiTotalLeaderboard<MaimaiTotalPlayedLeaderboardItem>(
                            authToken = token,
                            gameType = 1
                        )
                    )
                }
            }
            _uiState.update {
                it.copy(
                    totalPlayedLeaderboard = it.maimaiTotalPlayedLeaderboardRawData.mapIndexed { index, item ->
                        HomeLeaderboardRow(
                            index = index,
                            uid = item.uid.toString(),
                            username = item.username,
                            nickname = item.nickname,
                            info = item.totalPlayed.toString()
                        )
                    }
                )
            }
        }
        viewModelScope.launch {
            if (!_uiState.value.doneLoadingMaimaiFirstLeaderboard) {
                _uiState.update {
                    it.copy(
                        doneLoadingMaimaiFirstLeaderboard = true,
                        maimaiFirstLeaderboardRawData = CFQServer.apiTotalLeaderboard<MaimaiFirstLeaderboardItem>(
                            authToken = token,
                            gameType = 1
                        )
                    )
                }
            }
            _uiState.update {
                it.copy(
                    firstLeaderboard = it.maimaiFirstLeaderboardRawData.mapIndexed { index, item ->
                        HomeLeaderboardRow(
                            index = index,
                            uid = item.uid.toString(),
                            username = item.username,
                            nickname = item.nickname,
                            info = item.firstCount.toString(),
                            extraInfo = item.firstMusics
                        )
                    }
                )
            }
        }
    }
}