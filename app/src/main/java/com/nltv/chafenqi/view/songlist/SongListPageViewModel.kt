package com.nltv.chafenqi.view.songlist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nltv.chafenqi.storage.CFQUser
import com.nltv.chafenqi.storage.`object`.CFQPersistentData
import com.nltv.chafenqi.storage.songlist.MusicEntry
import com.nltv.chafenqi.storage.songlist.chunithm.ChunithmMusicEntry
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiMusicEntry
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

class SongListPageViewModel : ViewModel() {
    val user = CFQUser

    val maiMusicList = CFQPersistentData.Maimai.musicList
    val chuMusicList = CFQPersistentData.Chunithm.musicList

    var searchQuery by mutableStateOf("")
        private set
    var isSearchBarActive by mutableStateOf(false)

    private val maiSearchFlow = flowOf(maiMusicList)
    val maiSearchResult: StateFlow<List<MaimaiMusicEntry>> =
        snapshotFlow { searchQuery }
            .combine(maiSearchFlow) { query, musicList ->
                when {
                    user.mode == 0 -> emptyList()
                    query.isNotEmpty() -> {
                        musicList.filter { entry ->
                            entry.title.contains(
                                query,
                                ignoreCase = true
                            ) || entry.basicInfo.artist.contains(query, ignoreCase = true)
                        }
                    }

                    else -> emptyList()
                }
            }
            .stateIn(
                scope = viewModelScope,
                initialValue = emptyList(),
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLS)
            )

    private val chuSearchFlow = flowOf(chuMusicList)
    val chuSearchResult: StateFlow<List<ChunithmMusicEntry>> =
        snapshotFlow { searchQuery }
            .combine(chuSearchFlow) { query, musicList ->
                when {
                    user.mode == 1 -> emptyList()
                    query.isNotEmpty() -> {
                        musicList.filter { entry ->
                            entry.title.contains(query, ignoreCase = true) || entry.artist.contains(
                                query,
                                ignoreCase = true
                            )
                        }
                    }

                    else -> emptyList()
                }
            }
            .stateIn(
                scope = viewModelScope,
                initialValue = emptyList(),
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLS)
            )

    fun getMusicList(): List<MusicEntry> {
        return when (user.mode) {
            0 -> chuMusicList
            1 -> maiMusicList
            else -> emptyList()
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        searchQuery = newQuery
    }

    companion object {
        private const val TIMEOUT_MILLS = 5_000L
    }
}
