package com.nltv.chafenqi.storage.room

import android.content.Context
import com.nltv.chafenqi.storage.room.user.maimai.LocalUserMaimaiDataRepository
import com.nltv.chafenqi.storage.room.user.maimai.UserMaimaiDataRepository
import com.nltv.chafenqi.storage.room.user.maimai.UserMaimaiDatabase

interface RoomContainer {
    val userMaiDataRepository: UserMaimaiDataRepository
}

class RoomDataContainer(private val context: Context): RoomContainer {
    override val userMaiDataRepository: UserMaimaiDataRepository by lazy {
        LocalUserMaimaiDataRepository(UserMaimaiDatabase.getDatabase(context).UserMaimaiDataDao())
    }
}