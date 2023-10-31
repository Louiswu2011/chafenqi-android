package com.nltv.chafenqi.view.home

import androidx.lifecycle.ViewModel
import com.nltv.chafenqi.storage.CFQUser
import com.nltv.chafenqi.storage.`object`.CFQPersistentData
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiMusicEntry

class HomePageViewModel(

): ViewModel() {
    val user = CFQUser

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
