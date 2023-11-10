package com.nltv.chafenqi.view.songlist

import androidx.lifecycle.ViewModel
import com.nltv.chafenqi.storage.CFQUser
import com.nltv.chafenqi.storage.`object`.CFQPersistentData
import com.nltv.chafenqi.storage.songlist.MusicEntry

class SongListPageViewModel(

) : ViewModel() {
    val user = CFQUser

    private val maiMusicList = CFQPersistentData.Maimai.musicList
    private val chuMusicList = CFQPersistentData.Chunithm.musicList

    fun getMusicList(): List<MusicEntry> {
        return when (user.mode) {
            0 -> chuMusicList
            1 -> maiMusicList
            else -> emptyList()
        }
    }

    companion object {
        private const val TIMEOUT_MILLS = 5_000L
    }
}
