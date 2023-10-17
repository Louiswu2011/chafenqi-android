package com.nltv.chafenqi.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.nltv.chafenqi.UIState
import com.nltv.chafenqi.storage.CFQUser
import com.nltv.chafenqi.storage.room.maimai.MaimaiMusicData
import com.nltv.chafenqi.storage.room.maimai.MaimaiMusicEntry
import com.nltv.chafenqi.storage.room.maimai.MaimaiMusicListRepository

class LoginPageViewModel(private val maiListRepo: MaimaiMusicListRepository): ViewModel() {
    var loginState by mutableStateOf(UIState.Pending)
    var user by mutableStateOf(CFQUser())

    suspend fun updateMaiList(list: List<MaimaiMusicEntry>) {
        list.onEach {
            maiListRepo.insertMusic(it)
        }
    }

}