package com.nltv.chafenqi.view.home.recent

import androidx.lifecycle.ViewModel
import com.nltv.chafenqi.storage.datastore.user.RecentScoreEntry
import com.nltv.chafenqi.storage.user.CFQUser

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
}