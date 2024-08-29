package com.nltv.chafenqi.view.songlist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nltv.chafenqi.extension.CHUNITHM_GENRE_STRINGS
import com.nltv.chafenqi.extension.CHUNITHM_LEVEL_STRINGS
import com.nltv.chafenqi.extension.CHUNITHM_VERSION_STRINGS
import com.nltv.chafenqi.extension.MAIMAI_GENRE_STRINGS
import com.nltv.chafenqi.extension.MAIMAI_LEVEL_STRINGS
import com.nltv.chafenqi.extension.MAIMAI_VERSION_STRINGS
import com.nltv.chafenqi.extension.toLevelIndex
import com.nltv.chafenqi.storage.persistent.CFQPersistentData
import com.nltv.chafenqi.storage.songlist.chunithm.ChunithmMusicEntry
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiMusicEntry
import com.nltv.chafenqi.storage.user.CFQUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SongListUiState(
    val maiMusicList: List<MaimaiMusicEntry> = CFQPersistentData.Maimai.musicList,
    val chuMusicList: List<ChunithmMusicEntry> = CFQPersistentData.Chunithm.musicList,
    val maiSearchResult: List<MaimaiMusicEntry> = listOf(),
    val chuSearchResult: List<ChunithmMusicEntry> = listOf()
)

class SongListPageViewModel : ViewModel() {
    val user = CFQUser
    private val _uiState = MutableStateFlow(SongListUiState())
    val uiState: StateFlow<SongListUiState> = _uiState.asStateFlow()

    val origMaiMusicList = CFQPersistentData.Maimai.musicList
    val origChuMusicList = CFQPersistentData.Chunithm.musicList

    var searchQuery by mutableStateOf("")
    var isSearchBarActive by mutableStateOf(false)

    var filterFavorite by mutableStateOf(false)
    var filterPlayed by mutableStateOf(false)

    var filterLevel by mutableStateOf(false)
    var filterMaimaiLevelList =
        MutableList(MAIMAI_LEVEL_STRINGS.size) { false }.toMutableStateList()
    var filterChunithmLevelList =
        MutableList(CHUNITHM_LEVEL_STRINGS.size) { false }.toMutableStateList()

    var filterConstant by mutableStateOf(false)
    var filterConstantUpperBound by mutableFloatStateOf(0f)
    var filterConstantLowerBound by mutableFloatStateOf(0f)

    var filterGenre by mutableStateOf(false)
    var filterMaimaiGenreList =
        MutableList(MAIMAI_GENRE_STRINGS.size) { false }.toMutableStateList()
    var filterChunithmGenreList =
        MutableList(CHUNITHM_GENRE_STRINGS.size) { false }.toMutableStateList()

    var filterVersion by mutableStateOf(false)
    var filterMaimaiVersionList =
        MutableList(MAIMAI_VERSION_STRINGS.size) { false }.toMutableStateList()
    var filterChunithmVersionList =
        MutableList(CHUNITHM_VERSION_STRINGS.size) { false }.toMutableStateList()

    var filterEnabled = false

    var showFilterLevelDialog by mutableStateOf(false)
    var showFilterConstantDialog by mutableStateOf(false)
    var showFilterGenreDialog by mutableStateOf(false)
    var showFilterVersionDialog by mutableStateOf(false)

    private fun testFilterEnabled(): Boolean {
        return filterPlayed || filterLevel || filterConstant || filterGenre || filterVersion
    }

    fun update() {
        viewModelScope.launch {
            _uiState.update { currentValue ->
                when (user.mode) {
                    0 -> {
                        val filteredList = filterChunithmList(CFQPersistentData.Chunithm.musicList)
                        currentValue.copy(
                            chuMusicList = filteredList,
                            chuSearchResult = searchChunithmList(filteredList)
                        )
                    }

                    1 -> {
                        val filteredList = filterMaimaiList(CFQPersistentData.Maimai.musicList)
                        currentValue.copy(
                            maiMusicList = filteredList,
                            maiSearchResult = searchMaimaiList(filteredList)
                        )
                    }

                    else -> {
                        currentValue
                    }
                }
            }
        }
        filterEnabled = testFilterEnabled()
    }

    private fun filterMaimaiList(orig: List<MaimaiMusicEntry>): List<MaimaiMusicEntry> {
        var result = orig
        if (filterFavorite) {
            result = result.filter { user.remoteOptions.maimaiFavList.split(",").contains(it.musicID) }
        }
        if (filterPlayed) {
            result = user.maimai.best.map { it.associatedMusicEntry }.distinct()
        }
        if (filterConstant && filterConstantLowerBound <= filterConstantUpperBound) {
            result = result.filter {
                for (constant in it.constants) {
                    if ((filterConstantLowerBound..filterConstantUpperBound).contains(constant)) {
                        return@filter true
                    }
                }
                return@filter false
            }
        }
        if (filterLevel && filterMaimaiLevelList.any { it }) {
            result = result.filter {
                for (level in it.level) {
                    val levelIndex = level.toLevelIndex(1)
                    if (levelIndex > 0 && filterMaimaiLevelList[level.toLevelIndex(1)]) {
                        return@filter true
                    }
                }
                return@filter false
            }
        }
        if (filterGenre && filterMaimaiGenreList.any { it }) {
            result = result.filter {
                filterMaimaiGenreList[MAIMAI_GENRE_STRINGS.indexOf(it.basicInfo.genre)]
            }
        }
        if (filterVersion && filterMaimaiVersionList.any { it }) {
            result = result.filter {
                filterMaimaiVersionList[MAIMAI_VERSION_STRINGS.indexOf(it.basicInfo.from)]
            }
        }
        return result
    }

    private fun searchMaimaiList(
        orig: List<MaimaiMusicEntry>,
        query: String = searchQuery
    ): List<MaimaiMusicEntry> {
        return when {
            user.mode == 0 -> emptyList()
            query.isNotEmpty() -> {
                orig.filter { entry ->
                    entry.title.contains(
                        query,
                        ignoreCase = true
                    ) || entry.basicInfo.artist.contains(query, ignoreCase = true)
                }
            }

            else -> emptyList()
        }
    }

    private fun filterChunithmList(orig: List<ChunithmMusicEntry>): List<ChunithmMusicEntry> {
        var result = orig
        if (filterFavorite) {
            result = result.filter { user.remoteOptions.chunithmFavList.split(",").contains(it.musicID.toString()) }
        }
        if (filterPlayed) {
            result = user.chunithm.best.map { it.associatedMusicEntry }.distinct()
        }
        if (filterConstant && filterConstantLowerBound <= filterConstantUpperBound) {
            result = result.filter {
                for (constant in it.charts.constants) {
                    if (constant > 0.0 && (filterConstantLowerBound..filterConstantUpperBound).contains(
                            constant
                        )
                    ) {
                        return@filter true
                    }
                }
                return@filter false
            }
        }
        if (filterLevel && filterChunithmLevelList.isNotEmpty()) {
            result = result.filter {
                for (level in it.charts.levels) {
                    if (level != "0" && filterChunithmLevelList[level.toLevelIndex(0)]) {
                        return@filter true
                    }
                }
                return@filter false
            }
        }
        if (filterGenre && filterChunithmGenreList.any { it }) {
            result = result.filter {
                filterChunithmGenreList[CHUNITHM_GENRE_STRINGS.indexOf(it.genre)]
            }
        }
        if (filterVersion && filterChunithmVersionList.any { it }) {
            result = result.filter {
                filterChunithmVersionList[CHUNITHM_VERSION_STRINGS.indexOf(it.from)]
            }
        }
        return result
    }

    private fun searchChunithmList(
        orig: List<ChunithmMusicEntry>,
        query: String = searchQuery
    ): List<ChunithmMusicEntry> {
        return when {
            user.mode == 1 -> emptyList()
            query.isNotEmpty() -> {
                orig.filter { entry ->
                    entry.title.contains(query, ignoreCase = true) || entry.artist.contains(
                        query,
                        ignoreCase = true
                    )
                }
            }

            else -> emptyList()
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        searchQuery = newQuery
        when (user.mode) {
            0 -> {
                _uiState.update { currentValue ->
                    currentValue.copy(
                        chuSearchResult = searchChunithmList(currentValue.chuMusicList, newQuery)
                    )
                }
            }

            1 -> {
                _uiState.update { currentValue ->
                    currentValue.copy(
                        maiSearchResult = searchMaimaiList(currentValue.maiMusicList, newQuery)
                    )
                }
            }
        }
    }

    companion object {
        private const val TIMEOUT_MILLS = 5_000L
    }
}
