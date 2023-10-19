package com.nltv.chafenqi.storage.room

import android.content.Context
import com.nltv.chafenqi.storage.room.songlist.chunithm.ChunithmMusicListDao
import com.nltv.chafenqi.storage.room.songlist.chunithm.ChunithmMusicListDatabase
import com.nltv.chafenqi.storage.room.songlist.chunithm.ChunithmMusicListRepository
import com.nltv.chafenqi.storage.room.songlist.chunithm.LocalChunithmMusicListRepository
import com.nltv.chafenqi.storage.room.songlist.maimai.LocalMaimaiMusicListRepository
import com.nltv.chafenqi.storage.room.songlist.maimai.MaimaiMusicListDatabase
import com.nltv.chafenqi.storage.room.songlist.maimai.MaimaiMusicListRepository
import com.nltv.chafenqi.storage.room.user.maimai.LocalUserMaimaiDataRepository
import com.nltv.chafenqi.storage.room.user.maimai.UserMaimaiDataRepository
import com.nltv.chafenqi.storage.room.user.maimai.UserMaimaiDatabase

interface RoomContainer {
    val maiListRepository: MaimaiMusicListRepository
    val chuListRepository: ChunithmMusicListRepository
    val userMaiDataRepository: UserMaimaiDataRepository
}

class RoomDataContainer(private val context: Context): RoomContainer {
    override val maiListRepository: MaimaiMusicListRepository by lazy {
        LocalMaimaiMusicListRepository(MaimaiMusicListDatabase.getDatabase(context).MaimaiMusicListDao())
    }

    override val chuListRepository: ChunithmMusicListRepository by lazy {
        LocalChunithmMusicListRepository(ChunithmMusicListDatabase.getDatabase(context).ChunithmMusicListDao())
    }

    override val userMaiDataRepository: UserMaimaiDataRepository by lazy {
        LocalUserMaimaiDataRepository(UserMaimaiDatabase.getDatabase(context).UserMaimaiDataDao())
    }
}