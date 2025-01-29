package com.nltv.chafenqi.view.home.recent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nltv.chafenqi.model.user.chunithm.UserChunithmRecentScoreEntry
import com.nltv.chafenqi.model.user.maimai.UserMaimaiRecentScoreEntry
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
    var maiRecentList: List<UserMaimaiRecentScoreEntry> = listOf(),
    var chuRecentList: List<UserChunithmRecentScoreEntry> = listOf(),
    var currentPageIndex: Int = 0,
)

class HomeRecentViewModel : ViewModel() {
    val user = CFQUser

    private val entriesPerPage = 30.0
    val chuAvailablePage = ceil(user.chunithm.recent.size / entriesPerPage)
    val maiAvailablePage = ceil(user.maimai.recent.size / entriesPerPage)

    val maiRecentList = user.maimai.recent
    val chuRecentList = user.chunithm.recent

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
                                min(((index + 1) * entriesPerPage).toInt(), chuRecentList.size)
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
                                min(((index + 1) * entriesPerPage).toInt(), maiRecentList.size)
                            )
                        )
                    }
                }
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