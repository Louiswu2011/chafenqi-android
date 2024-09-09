package com.nltv.chafenqi.view.home.log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nltv.chafenqi.storage.datastore.user.chunithm.ChunithmRecentScoreEntry
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiRecentScoreEntry
import com.nltv.chafenqi.storage.user.CFQUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class LogDetailPageViewModel : ViewModel() {
    data class LogDetailPageUiState(
        val currentRating: String = "",
        val currentPlayCount: String = "",
        val ratingGain: String = "",
        val playCountGain: String = "",
        val ratingGainIndicator: String = "",
        val playCountGainIndicator: String = "",
        val maimaiEntries: List<MaimaiRecentScoreEntry> = listOf(),
        val chunithmEntries: List<ChunithmRecentScoreEntry> = listOf()
    )

    private val _uiState = MutableStateFlow(LogDetailPageUiState())
    val uiState = _uiState.asStateFlow()

    fun update(mode: Int, index: Int) {
        when (mode) {
            0 -> updateChunithm(index)
            1 -> updateMaimai(index)
        }
    }

    private fun updateMaimai(index: Int) {
        val log = CFQUser.maimai.log ?: return
        val entry = log.records.getOrNull(index) ?: return

        var ratingGain = 0

        val previousEntry = log.records.getOrNull(index + 1)
        if (previousEntry != null) {
            ratingGain = (entry.latestDeltaEntry.rating - previousEntry.latestDeltaEntry.rating)
        }
        val playCountGain = entry.recentEntries.size
        val totalPlayCount = log.records.filter { it.date.epochSeconds <= entry.date.epochSeconds }
            .sumOf { it.recentEntries.size }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    currentRating = if (previousEntry == null || previousEntry.latestDeltaEntry.rating == 0) "无数据" else entry.latestDeltaEntry.rating.toString(),
                    currentPlayCount = totalPlayCount.toString(),
                    ratingGain = ratingGain.toString(),
                    playCountGain = playCountGain.toString(),
                    ratingGainIndicator = if (ratingGain > 0) "+" else if (ratingGain < 0) "-" else "\u00b1",
                    playCountGainIndicator = if (playCountGain > 0) "+" else "\u00b1",
                    maimaiEntries = entry.recentEntries
                )
            }
        }
    }

    private fun updateChunithm(index: Int) {
        val log = CFQUser.chunithm.log ?: return
        val entry = log.records.getOrNull(index) ?: return

        var ratingGain = 0.0
        val playCountGain = entry.recentEntries.size
        val totalPlayCount = log.records.filter { it.date.epochSeconds <= entry.date.epochSeconds }
            .sumOf { it.recentEntries.size }

        val previousEntry = log.records.getOrNull(index + 1)
        if (previousEntry != null) {
            ratingGain = (entry.latestDeltaEntry.rating - previousEntry.latestDeltaEntry.rating)
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    currentRating = if (previousEntry == null || previousEntry.latestDeltaEntry.rating == 0.0) "无数据" else entry.latestDeltaEntry.rating.toString(),
                    currentPlayCount = totalPlayCount.toString(),
                    ratingGain = String.format(Locale.getDefault(), "%.2f", ratingGain),
                    playCountGain = playCountGain.toString(),
                    ratingGainIndicator = if (ratingGain > 0) "+" else if (ratingGain < 0) "-" else "\u00b1",
                    playCountGainIndicator = if (playCountGain > 0) "+" else "\u00b1",
                    chunithmEntries = entry.recentEntries
                )
            }
        }
    }
}