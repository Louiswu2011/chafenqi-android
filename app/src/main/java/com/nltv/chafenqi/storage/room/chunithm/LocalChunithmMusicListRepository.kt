package com.nltv.chafenqi.storage.room.chunithm

import kotlinx.coroutines.flow.Flow

class LocalChunithmMusicListRepository(private val musicDataDao: ChunithmMusicListDao): ChunithmMusicListRepository {
    override fun getAllMusicStream(): Flow<List<ChunithmMusicEntry>> = musicDataDao.getMusicList()

    override fun getMusicStreamById(id: String): Flow<ChunithmMusicEntry?> = musicDataDao.getMusicById(id)

    override suspend fun insertMusic(musicData: ChunithmMusicEntry) = musicDataDao.insert(musicData)

    override suspend fun deleteMusic(musicData: ChunithmMusicEntry) = musicDataDao.delete(musicData)

    override suspend fun updateMusic(musicData: ChunithmMusicEntry) = musicDataDao.update(musicData)
}