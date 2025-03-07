package com.nltv.chafenqi.view.info.maimai.level

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nltv.chafenqi.extension.MAIMAI_LEVEL_STRINGS
import com.nltv.chafenqi.model.user.maimai.UserMaimaiBestScoreEntry
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiMusicEntry
import com.nltv.chafenqi.storage.user.CFQUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class InfoMaimaiLevelsUiState(
    val levelEntries: List<UserMaimaiBestScoreEntry> = listOf(),
    val musicEntries: List<MaimaiMusicEntry> = listOf(),
    val rateSizes: List<Int> = listOf(),
    val entrySize: Int = 0,
)

class InfoMaimaiLevelsViewModel : ViewModel() {
    private val levelInfo = CFQUser.maimai.aux.levelInfo

    private val _uiState = MutableStateFlow(InfoMaimaiLevelsUiState())
    val uiState = _uiState.asStateFlow()

    var currentPosition by mutableIntStateOf(0)
    var isLoaded = false

    private fun setCurrentLevel(level: String) {
        Log.i("Levels", "Current Level is $level")
        viewModelScope.launch {
            val info = levelInfo.firstOrNull { it.levelString == level }
            if (info != null) {
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
        if (position > 24 || position < 0) return
        currentPosition = position
        setCurrentLevel(MAIMAI_LEVEL_STRINGS[currentPosition])
    }

    fun increaseLevel() {
        if (currentPosition == 24) return
        currentPosition += 1
        setCurrentLevel(MAIMAI_LEVEL_STRINGS[currentPosition])
    }

    fun decreaseLevel() {
        if (currentPosition == 0) return
        currentPosition -= 1
        setCurrentLevel(MAIMAI_LEVEL_STRINGS[currentPosition])
    }
}