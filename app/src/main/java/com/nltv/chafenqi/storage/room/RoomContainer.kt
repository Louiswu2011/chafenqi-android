package com.nltv.chafenqi.storage.room

import android.content.Context
import com.nltv.chafenqi.storage.room.maimai.LocalMaimaiMusicListRepository
import com.nltv.chafenqi.storage.room.maimai.MaimaiMusicListDatabase
import com.nltv.chafenqi.storage.room.maimai.MaimaiMusicListRepository

interface RoomContainer {
    val maiListRepository: MaimaiMusicListRepository
}

class RoomDataContainer(private val context: Context): RoomContainer {
    override val maiListRepository: MaimaiMusicListRepository by lazy {
        LocalMaimaiMusicListRepository(MaimaiMusicListDatabase.getDatabase(context).MaimaiMusicListDao())
    }


}