package com.nltv.chafenqi.storage.room.songlist.maimai

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MaimaiMusicListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: MaimaiMusicEntry)

    @Update
    suspend fun update(item: MaimaiMusicEntry)

    @Delete
    suspend fun delete(item: MaimaiMusicEntry)

    @Query("SELECT * from MaimaiMusicList where id = :id")
    fun getMusicById(id: String): Flow<MaimaiMusicEntry>

    @Query("SELECT * from MaimaiMusicList")
    fun getMusicList(): Flow<List<MaimaiMusicEntry>>

    @Query("SELECT COUNT(uid) from MaimaiMusicList")
    fun getMusicCount(): Int
}