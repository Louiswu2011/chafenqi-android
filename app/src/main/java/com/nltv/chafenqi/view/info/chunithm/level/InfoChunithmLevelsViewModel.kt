package com.nltv.chafenqi.view.info.chunithm.level

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nltv.chafenqi.extension.LEVEL_STRINGS
import com.nltv.chafenqi.storage.datastore.user.chunithm.ChunithmBestScoreEntry
import com.nltv.chafenqi.storage.songlist.chunithm.ChunithmMusicEntry
import com.nltv.chafenqi.storage.user.CFQUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class InfoChunithmLevelUiState(
    val levelEntries: List<ChunithmBestScoreEntry> = listOf(),
    val musicEntries: List<ChunithmMusicEntry> = listOf(),
    val rateSizes: List<Int> = listOf(),
    val entrySize: Int = 0
)

class InfoChunithmLevelsViewModel: ViewModel() {
    private val levelInfo = CFQUser.chunithm.aux.levelInfo

    private val _uiState = MutableStateFlow(InfoChunithmLevelUiState())
    val uiState = _uiState.asStateFlow()

    var currentPosition by mutableIntStateOf(0)

    private fun setCurrentLevel(level: String) {
        viewModelScope.launch {
            val info = levelInfo.firstOrNull { it.levelString == level }
            if (info != null) {
                Log.i("ChunithmLevels", info.entryPerRate.toString())
                _uiState.update { currentValue ->
                    currentValue.copy(
                        levelEntries = info.playedBestEntries,
                        musicEntries = info.notPlayedMusicEntries,
                        rateSizes = info.entryPerRate,
                        entrySize = info.musicCount
                    )
                }
            }
        }
    }

    fun assignCurrentPosition(position: Int) {
        if (position > 22 || position < 0) return
        currentPosition = position
        setCurrentLevel(LEVEL_STRINGS[currentPosition])
    }

    fun increaseLevel() {
        if (currentPosition == 22) return
        currentPosition += 1
        setCurrentLevel(LEVEL_STRINGS[currentPosition])
    }

    fun decreaseLevel() {
        if (currentPosition == 0) return
        currentPosition -= 1
        setCurrentLevel(LEVEL_STRINGS[currentPosition])
    }
}