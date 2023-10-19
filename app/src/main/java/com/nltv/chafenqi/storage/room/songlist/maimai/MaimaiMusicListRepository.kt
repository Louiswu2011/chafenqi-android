package com.nltv.chafenqi.storage.room.songlist.maimai

import kotlinx.coroutines.flow.Flow

interface MaimaiMusicListRepository {

    fun getAllMusicStream(): Flow<List<MaimaiMusicEntry>>
    fun getMusicStreamById(id: String): Flow<MaimaiMusicEntry?>

    suspend fun insertMusic(musicData: MaimaiMusicEntry)
    suspend fun deleteMusic(musicData: MaimaiMusicEntry)
    suspend fun updateMusic(musicData: MaimaiMusicEntry)

    suspend fun getMusicCount(): Int
}