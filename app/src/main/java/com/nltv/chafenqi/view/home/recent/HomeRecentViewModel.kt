package com.nltv.chafenqi.view.home.recent

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nltv.chafenqi.storage.datastore.user.RecentScoreEntry
import com.nltv.chafenqi.storage.user.CFQUser
import com.nltv.chafenqi.util.RecentSelectableDates
import kotlinx.coroutines.launch

class HomeRecentViewModel : ViewModel() {
    val user = CFQUser

    private val maiRecentList = user.maimai.recent
    private val chuRecentList = user.chunithm.recent

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