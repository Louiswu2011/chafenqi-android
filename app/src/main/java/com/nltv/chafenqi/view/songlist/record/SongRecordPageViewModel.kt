package com.nltv.chafenqi.view.songlist.record

import androidx.lifecycle.ViewModel
import com.nltv.chafenqi.storage.CFQUser
import com.nltv.chafenqi.storage.datastore.user.chunithm.ChunithmRecentScoreEntry
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiRecentScoreEntry
import com.nltv.chafenqi.storage.`object`.CFQPersistentData
import com.nltv.chafenqi.storage.songlist.chunithm.ChunithmMusicEntry
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiMusicEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SongRecordPageUiState(
    val maiHistoryEntries: List<MaimaiRecentScoreEntry> = listOf(),
    val chuHistoryEntries: List<ChunithmRecentScoreEntry> = listOf()
)

class SongRecordPageViewModel: ViewModel() {
    private var maiMusic = MaimaiMusicEntry()
    private var chuMusic = ChunithmMusicEntry()

    private val maiMusicList = CFQPersistentData.Maimai.musicList
    private val chuMusicList = CFQPersistentData.Chunithm.musicList

    private val maiRecentEntries = CFQUser.maimai.recent
    private val chuRecentEntries = CFQUser.chunithm.recent

    private val _uiState = MutableStateFlow(SongRecordPageUiState())
    val uiState: StateFlow<SongRecordPageUiState> = _uiState.asStateFlow()

    val mode = CFQUser.mode

    fun update(mode: Int, index: Int, levelIndex: Int) {
        when {
            mode == 0 && chuMusicList.isNotEmpty() -> {
                chuMusic = chuMusicList[index]
                val entries = chuRecentEntries.filter {
                    it.idx == chuMusic.musicID.toString() && it.levelIndex == levelIndex
                }
                _uiState.update { currentValue ->
                    currentValue.copy(
                        chuHistoryEntries = entries
                    )
                }
            }
            mode == 1 && maiMusicList.isNotEmpty() -> {
                maiMusic = maiMusicList[index]
                val entries = maiRecentEntries.filter {
                    it.associatedMusicEntry == maiMusic && it.levelIndex == levelIndex
                }
                _uiState.update { currentValue ->
                    currentValue.copy(
                        maiHistoryEntries = entries
                    )
                }
            }
        }
    }
}