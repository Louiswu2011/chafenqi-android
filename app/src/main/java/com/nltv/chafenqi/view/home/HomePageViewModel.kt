package com.nltv.chafenqi.view.home

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.nltv.chafenqi.storage.CFQUser
import com.nltv.chafenqi.storage.`object`.CFQPersistentData
import com.nltv.chafenqi.storage.room.songlist.maimai.MaimaiMusicEntry
import com.nltv.chafenqi.storage.room.user.maimai.MaimaiRecentScoreEntry
import com.nltv.chafenqi.storage.room.user.maimai.UserMaimaiDataRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class HomePageViewModel(
    private val userMaiRepo: UserMaimaiDataRepository
): ViewModel() {
    val userMaiRecentState: StateFlow<UserMaimaiRecentData> = userMaiRepo.getAllRecentScore().map { UserMaimaiRecentData(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLS),
            initialValue = UserMaimaiRecentData()
        )

    val userMaimaiInfo = CFQUser.maimaiUserInfo

    fun getAssociatedMaimaiMusic(title: String): MaimaiMusicEntry {
        val music = CFQPersistentData.Maimai.musicList.find {
            it.title == title
        }

        return music ?: MaimaiMusicEntry()
    }




    companion object {
        private const val TIMEOUT_MILLS = 5_000L
    }
}

data class UserMaimaiRecentData(val data: List<MaimaiRecentScoreEntry> = listOf())