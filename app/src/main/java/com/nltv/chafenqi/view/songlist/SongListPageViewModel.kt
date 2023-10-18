package com.nltv.chafenqi.view.songlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nltv.chafenqi.storage.room.chunithm.ChunithmMusicEntry
import com.nltv.chafenqi.storage.room.chunithm.ChunithmMusicListRepository
import com.nltv.chafenqi.storage.room.maimai.MaimaiMusicEntry
import com.nltv.chafenqi.storage.room.maimai.MaimaiMusicListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SongListPageViewModel(
    private val maiListRepo: MaimaiMusicListRepository,
    private val chuListRepo: ChunithmMusicListRepository
) : ViewModel() {
    val maiSongState: StateFlow<MaimaiListUiState> = maiListRepo.getAllMusicStream().map { MaimaiListUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLS),
            initialValue = MaimaiListUiState()
        )
    val chuSongState: StateFlow<ChunithmListUiState> = chuListRepo.getAllMusicStream().map { ChunithmListUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLS),
            initialValue = ChunithmListUiState()
        )

    companion object {
        private const val TIMEOUT_MILLS = 5_000L
    }
}

data class MaimaiListUiState(val items: List<MaimaiMusicEntry> = listOf())
data class ChunithmListUiState(val items: List<ChunithmMusicEntry> = listOf())