package com.nltv.chafenqi.view.home.recent

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nltv.chafenqi.storage.datastore.user.RecentScoreEntry
import com.nltv.chafenqi.storage.datastore.user.chunithm.ChunithmRecentScoreEntry
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiRecentScoreEntry
import com.nltv.chafenqi.storage.user.CFQUser
import com.nltv.chafenqi.util.RecentSelectableDates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.min

data class HomeRecentViewUiState(
    var maiRecentList: List<MaimaiRecentScoreEntry> = listOf(),
    var chuRecentList: List<ChunithmRecentScoreEntry> = listOf(),
    var currentPageIndex: Int = 0,
)

class HomeRecentViewModel : ViewModel() {
    val user = CFQUser

    private val entriesPerPage = 30.0
    val chuAvailablePage = ceil(user.chunithm.recent.size / entriesPerPage)
    val maiAvailablePage = ceil(user.maimai.recent.size / entriesPerPage)

    private val maiRecentList = user.maimai.recent
    private val chuRecentList = user.chunithm.recent

    private val _uiState = MutableStateFlow(HomeRecentViewUiState())
    val uiState: StateFlow<HomeRecentViewUiState> = _uiState.asStateFlow()

    fun updatePage(index: Int) {
        when (user.mode) {
            0 -> {
                if (index >= chuAvailablePage) return
                viewModelScope.launch {
                    _uiState.update {
                        it.copy(
                            currentPageIndex = index,
                            chuRecentList = chuRecentList.subList(
                                (index * entriesPerPage).toInt(),
                                min(((index + 1) * entriesPerPage).toInt(), chuRecentList.lastIndex)
                            )
                        )
                    }
                }
            }
            1 -> {
                if (index >= maiAvailablePage) return
                viewModelScope.launch {
                    _uiState.update {
                        it.copy(
                            currentPageIndex = index,
                            maiRecentList = maiRecentList.subList(
                                (index * entriesPerPage).toInt(),
                                min(((index + 1) * entriesPerPage).toInt(), maiRecentList.lastIndex)
                            )
                        )
                    }
                }
            }
        }
    }

    fun getRecentList(): List<RecentScoreEntry> {
        return when (user.mode) {
            0 -> {
                chuRecentList
            }

            1 -> {
                maiRecentList
            }

            else -> {
                listOf()
            }
        }
    }

    fun getRecentSelectableDates(): RecentSelectableDates {
        return when (user.mode) {
            0 -> {
                RecentSelectableDates(
                    oldestMills = chuRecentList.lastOrNull()?.timestamp?.times(1000L) ?: 0,
                    latestMills = chuRecentList.firstOrNull()?.timestamp?.times(1000L) ?: 0
                )
            }
            1 -> {
                RecentSelectableDates(
                    oldestMills = maiRecentList.lastOrNull()?.timestamp?.times(1000L) ?: 0,
                    latestMills = maiRecentList.firstOrNull()?.timestamp?.times(1000L) ?: 0
                )
            }

            else -> RecentSelectableDates(0, 0)
        }
    }

}