package com.nltv.chafenqi.storage.room

import android.content.Context
import com.nltv.chafenqi.storage.room.chunithm.ChunithmMusicListDao
import com.nltv.chafenqi.storage.room.chunithm.ChunithmMusicListDatabase
import com.nltv.chafenqi.storage.room.chunithm.ChunithmMusicListRepository
import com.nltv.chafenqi.storage.room.chunithm.LocalChunithmMusicListRepository
import com.nltv.chafenqi.storage.room.maimai.LocalMaimaiMusicListRepository
import com.nltv.chafenqi.storage.room.maimai.MaimaiMusicListDatabase
import com.nltv.chafenqi.storage.room.maimai.MaimaiMusicListRepository

interface RoomContainer {
    val maiListRepository: MaimaiMusicListRepository
    val chuListRepository: ChunithmMusicListRepository
}

class RoomDataContainer(private val context: Context): RoomContainer {
    override val maiListRepository: MaimaiMusicListRepository by lazy {
        LocalMaimaiMusicListRepository(MaimaiMusicListDatabase.getDatabase(context).MaimaiMusicListDao())
    }

    override val chuListRepository: ChunithmMusicListRepository by lazy {
        LocalChunithmMusicListRepository(ChunithmMusicListDatabase.getDatabase(context).ChunithmMusicListDao())
    }
}