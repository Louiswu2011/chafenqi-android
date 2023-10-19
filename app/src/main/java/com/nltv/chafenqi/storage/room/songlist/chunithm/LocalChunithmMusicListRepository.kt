package com.nltv.chafenqi.storage.room.songlist.chunithm

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach

class LocalChunithmMusicListRepository(private val musicDataDao: ChunithmMusicListDao):
    ChunithmMusicListRepository {
    override fun getAllMusicStream(): Flow<List<ChunithmMusicEntry>> = musicDataDao.getMusicList()

    override fun getMusicStreamById(id: String): Flow<ChunithmMusicEntry?> = musicDataDao.getMusicById(id)

    override suspend fun insertMusic(musicData: ChunithmMusicEntry) = musicDataDao.insert(musicData)

    override suspend fun deleteMusic(musicData: ChunithmMusicEntry) = musicDataDao.delete(musicData)

    override suspend fun updateMusic(musicData: ChunithmMusicEntry) = musicDataDao.update(musicData)
    override suspend fun getMusicCount(): Int = musicDataDao.getMusicCount()
}