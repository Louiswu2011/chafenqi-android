package com.nltv.chafenqi.view.songlist.stats

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nltv.chafenqi.data.ChunithmLeaderboard
import com.nltv.chafenqi.data.ChunithmMusicStat
import com.nltv.chafenqi.data.MaimaiLeaderboard
import com.nltv.chafenqi.networking.CFQServer
import com.nltv.chafenqi.storage.persistent.CFQPersistentData
import com.nltv.chafenqi.storage.songlist.chunithm.ChunithmMusicEntry
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiMusicEntry
import com.nltv.chafenqi.view.songlist.ChunithmDifficultyInfo
import com.nltv.chafenqi.view.songlist.MaimaiDifficultyInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SongStatsUiState(
    val doneLoadingLeaderboard: Boolean = false,
    val doneLoadingStats: Boolean = false,
    val chunithmMusicEntry: ChunithmMusicEntry = ChunithmMusicEntry(),
    val chunithmLeaderboard: ChunithmLeaderboard = listOf(),
    val chunithmMusicStat: ChunithmMusicStat = ChunithmMusicStat(),
    val maimaiMusicEntry: MaimaiMusicEntry = MaimaiMusicEntry(),
    val maimaiLeaderboard: MaimaiLeaderboard = listOf()
)

data class SongStatsTabItem(
    val title: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
)

class SongStatsPageViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(SongStatsUiState())
    val statsState = _uiState.asStateFlow()

    val statsTabs = listOf(
        SongStatsTabItem(
            title = "排行榜",
            unselectedIcon = Icons.Outlined.Leaderboard,
            selectedIcon = Icons.Filled.Leaderboard
        ),
        SongStatsTabItem(
            title = "统计信息",
            unselectedIcon = Icons.Outlined.PieChart,
            selectedIcon = Icons.Filled.PieChart
        ),
        SongStatsTabItem(
            title = "游玩记录",
            unselectedIcon = Icons.Outlined.History,
            selectedIcon = Icons.Filled.History
        )
    )

    fun loadSong(mode: Int, index: Int) {
        if (mode == 0 && CFQPersistentData.Chunithm.musicList.isNotEmpty()) {
            val chuMusic = CFQPersistentData.Chunithm.musicList.getOrNull(index)
            if (chuMusic != null) {
                _uiState.update { currentValue ->
                    currentValue.copy(
                        chunithmMusicEntry = chuMusic
                    )
                }
            }
        } else if (mode == 1 && CFQPersistentData.Maimai.musicList.isNotEmpty()) {
            val maiMusic = CFQPersistentData.Maimai.musicList.getOrNull(index)
            if (maiMusic != null) {
                _uiState.update { currentValue ->
                    currentValue.copy(
                        maimaiMusicEntry = maiMusic
                    )
                }
            }
        }
    }

    fun fetchLeaderboard(mode: Int, index: Int, difficulty: Int, type: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            if (mode == 0 && CFQPersistentData.Chunithm.musicList.isNotEmpty()) {
                val chuMusic = CFQPersistentData.Chunithm.musicList.getOrNull(index)
                if (chuMusic != null) {
                    val result = CFQServer.apiChunithmLeaderboard(chuMusic.musicID, difficulty)
                    _uiState.update { currentValue ->
                        currentValue.copy(
                            doneLoadingLeaderboard = true,
                            chunithmLeaderboard = result
                        )
                    }
                }
            } else if (mode == 1 && CFQPersistentData.Maimai.musicList.isNotEmpty()) {
                val maiMusic = CFQPersistentData.Maimai.musicList.getOrNull(index)
                if (maiMusic != null) {
                    val result = CFQServer.apiMaimaiLeaderboard(maiMusic.musicID.toInt(), type, difficulty)
                    _uiState.update { currentValue ->
                        currentValue.copy(
                            doneLoadingLeaderboard = true,
                            maimaiLeaderboard = result
                        )
                    }
                }
            }
        }
    }

    fun fetchStats(mode: Int, index: Int, difficulty: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            if (mode == 0 && CFQPersistentData.Chunithm.musicList.isNotEmpty()) {
                val chuMusic = CFQPersistentData.Chunithm.musicList.getOrNull(index)
                if (chuMusic != null) {
                    val result = CFQServer.apiChunithmMusicStat(chuMusic.musicID, difficulty)
                    _uiState.update { currentValue ->
                        currentValue.copy(
                            doneLoadingStats = true,
                            chunithmMusicStat = result
                        )
                    }
                }
            }
        }
    }


}