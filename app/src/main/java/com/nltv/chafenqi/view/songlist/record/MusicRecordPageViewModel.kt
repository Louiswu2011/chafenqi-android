package com.nltv.chafenqi.view.songlist.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nltv.chafenqi.extension.toMonthDayString
import com.nltv.chafenqi.storage.datastore.user.chunithm.ChunithmRecentScoreEntry
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiRecentScoreEntry
import com.nltv.chafenqi.storage.persistent.CFQPersistentData
import com.nltv.chafenqi.storage.songlist.chunithm.ChunithmMusicEntry
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiMusicEntry
import com.nltv.chafenqi.storage.user.CFQUser
import com.patrykandpatrick.vico.core.cartesian.data.AxisValueOverrider
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MusicRecordPageUiState(
    val maiHistoryEntries: List<MaimaiRecentScoreEntry> = listOf(),
    val chuHistoryEntries: List<ChunithmRecentScoreEntry> = listOf(),
    val maiHistoryDateStringMap: MutableMap<Int, String> = mutableMapOf(),
    val chuHistoryDateStringMap: MutableMap<Int, String> = mutableMapOf(),
)

class MusicRecordPageViewModel : ViewModel() {
    private var maiMusic = MaimaiMusicEntry()
    private var chuMusic = ChunithmMusicEntry()

    private val maiMusicList = CFQPersistentData.Maimai.musicList
    private val chuMusicList = CFQPersistentData.Chunithm.musicList

    private val maiRecentEntries = CFQUser.maimai.recent
    private val chuRecentEntries = CFQUser.chunithm.recent

    private val _uiState = MutableStateFlow(MusicRecordPageUiState())
    val uiState: StateFlow<MusicRecordPageUiState> = _uiState.asStateFlow()

    val mode = CFQUser.mode

    val chuEntryProvider = CartesianChartModelProducer()
    val maiEntryProvider = CartesianChartModelProducer()

    fun update(mode: Int, index: Int, levelIndex: Int) {
        when {
            mode == 0 && chuMusicList.isNotEmpty() -> {
                chuMusic = chuMusicList[index]
                val map = mutableMapOf<Int, String>()
                val entries = chuRecentEntries.filter {
                    it.idx == chuMusic.musicID.toString() && it.levelIndex == levelIndex
                }
                entries.reversed().forEachIndexed { idx, entry ->
                    map[idx] = entry.timestamp.toMonthDayString()
                }
                _uiState.update { currentValue ->
                    currentValue.copy(
                        chuHistoryEntries = entries,
                        chuHistoryDateStringMap = map
                    )
                }
                viewModelScope.launch {
                    chuEntryProvider.runTransaction {
                        lineSeries {
                            series(entries.reversed().map { it.score })
                        }
                    }
                }
            }

            mode == 1 && maiMusicList.isNotEmpty() -> {
                maiMusic = maiMusicList[index]
                val map = mutableMapOf<Int, String>()
                val entries = maiRecentEntries.filter {
                    it.associatedMusicEntry == maiMusic && it.levelIndex == levelIndex
                }
                entries.reversed().forEachIndexed { idx, entry ->
                    map[idx] = entry.timestamp.toMonthDayString()
                }
                _uiState.update { currentValue ->
                    currentValue.copy(
                        maiHistoryEntries = entries,
                        maiHistoryDateStringMap = map
                    )
                }
                viewModelScope.launch {
                    maiEntryProvider.runTransaction {
                        lineSeries {
                            series(entries.reversed().map { it.achievements })
                        }
                    }
                }
            }
        }
    }
}