package com.nltv.chafenqi.view.home.team.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.nltv.chafenqi.storage.persistent.CFQPersistentData
import com.nltv.chafenqi.storage.songlist.chunithm.ChunithmMusicEntry
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiMusicEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class HomeTeamCourseSettingUiState(
    val chuMusic: List<ChunithmMusicEntry> = CFQPersistentData.Chunithm.musicList,
    val maiMusic: List<MaimaiMusicEntry> = CFQPersistentData.Maimai.musicList,

    val chuSearchResult: List<ChunithmMusicEntry> = listOf(),
    val maiSearchResult: List<MaimaiMusicEntry> = listOf(),
)

class HomeTeamCourseSettingViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(HomeTeamCourseSettingUiState())
    val uiState = _uiState.asStateFlow()

    var isSearchBarActive by mutableStateOf(false)
    var currentSelectedMusicSlot by mutableStateOf<Int?>(null)

    fun search(mode: Int, query: String) {
        when (mode) {
            0 -> {
                _uiState.update { state ->
                    state.copy(
                        chuSearchResult = if (query.isEmpty()) {
                            listOf()
                        } else {
                            _uiState.value.chuMusic.filter {
                                it.title.contains(
                                    query,
                                    ignoreCase = true
                                ) || it.artist.contains(query, ignoreCase = true)
                            }
                        }
                    )
                }
            }

            1 -> {
                _uiState.update { state ->
                    state.copy(
                        maiSearchResult = if (query.isEmpty()) {
                            listOf()
                        } else {
                            _uiState.value.maiMusic.filter {
                                it.title.contains(
                                    query,
                                    ignoreCase = true
                                ) || it.basicInfo.artist.contains(query, ignoreCase = true)
                            }
                        }
                    )
                }
            }
        }
    }
}