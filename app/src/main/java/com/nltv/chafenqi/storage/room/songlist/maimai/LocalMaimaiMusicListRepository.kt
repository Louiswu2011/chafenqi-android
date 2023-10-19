package com.nltv.chafenqi.storage.room.songlist.maimai

import kotlinx.coroutines.flow.Flow

class LocalMaimaiMusicListRepository(private val musicDataDao: MaimaiMusicListDao):
    MaimaiMusicListRepository {
    override fun getAllMusicStream(): Flow<List<MaimaiMusicEntry>> = musicDataDao.getMusicList()

    override fun getMusicStreamById(id: String): Flow<MaimaiMusicEntry?> = musicDataDao.getMusicById(id)

    override suspend fun insertMusic(musicData: MaimaiMusicEntry) = musicDataDao.insert(musicData)

    override suspend fun deleteMusic(musicData: MaimaiMusicEntry) = musicDataDao.delete(musicData)

    override suspend fun updateMusic(musicData: MaimaiMusicEntry) = musicDataDao.update(musicData)

    override suspend fun getMusicCount(): Int = musicDataDao.getMusicCount()
}