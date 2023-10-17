package com.nltv.chafenqi.storage.room.chunithm

import kotlinx.coroutines.flow.Flow

interface ChunithmMusicListRepository {
    fun getAllMusicStream(): Flow<List<ChunithmMusicEntry>>
    fun getMusicStreamById(id: String): Flow<ChunithmMusicEntry?>

    suspend fun insertMusic(musicData: ChunithmMusicEntry)
    suspend fun deleteMusic(musicData: ChunithmMusicEntry)
    suspend fun updateMusic(musicData: ChunithmMusicEntry)
}