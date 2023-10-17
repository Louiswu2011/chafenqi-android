package com.nltv.chafenqi.storage.room.chunithm

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ChunithmMusicListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ChunithmMusicEntry)

    @Update
    suspend fun update(item: ChunithmMusicEntry)

    @Delete
    suspend fun delete(item: ChunithmMusicEntry)

    @Query("SELECT * from ChunithmMusicList where musicId = :id")
    fun getMusicById(id: String): Flow<ChunithmMusicEntry>

    @Query("SELECT * from ChunithmMusicList")
    fun getMusicList(): Flow<List<ChunithmMusicEntry>>
}