package com.nltv.chafenqi.view.songlist

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nltv.chafenqi.storage.room.songlist.chunithm.ChunithmMusicEntry
import com.nltv.chafenqi.storage.room.songlist.chunithm.ChunithmMusicListRepository
import com.nltv.chafenqi.storage.room.songlist.maimai.MaimaiMusicEntry
import com.nltv.chafenqi.storage.room.songlist.maimai.MaimaiMusicListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SongListPageViewModel(
    private val maiListRepo: MaimaiMusicListRepository,
    private val chuListRepo: ChunithmMusicListRepository
) : ViewModel() {
    private var isLoading = MutableStateFlow(true)

    var isUiLoading = isLoading.asStateFlow()

    init {
        isLoading.update { false }
    }

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