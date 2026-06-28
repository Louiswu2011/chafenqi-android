package com.nltv.chafenqi.storage.db

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nltv.chafenqi.storage.db.entity.ChunithmMusicEntryEntity
import com.nltv.chafenqi.storage.db.entity.MaimaiGenreEntryEntity
import com.nltv.chafenqi.storage.db.entity.MaimaiMusicEntryEntity
import com.nltv.chafenqi.storage.db.entity.MaimaiVersionEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SongListDao {
    // Chunithm music
    @Query("SELECT * FROM chunithm_music ORDER BY music_id")
    fun getAllChunithmMusic(): Flow<List<ChunithmMusicEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllChunithmMusic(entries: List<ChunithmMusicEntryEntity>)

    @Query("DELETE FROM chunithm_music")
    suspend fun deleteAllChunithmMusic()

    @Query("SELECT COUNT(*) FROM chunithm_music")
    suspend fun getChunithmMusicCount(): Int

    // Maimai music
    @Query("SELECT * FROM maimai_music ORDER BY music_id")
    fun getAllMaimaiMusic(): Flow<List<MaimaiMusicEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMaimaiMusic(entries: List<MaimaiMusicEntryEntity>)

    @Query("DELETE FROM maimai_music")
    suspend fun deleteAllMaimaiMusic()

    @Query("SELECT COUNT(*) FROM maimai_music")
    suspend fun getMaimaiMusicCount(): Int

    // Maimai genre
    @Query("SELECT * FROM maimai_genre ORDER BY id")
    fun getAllMaimaiGenres(): Flow<List<MaimaiGenreEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMaimaiGenres(entries: List<MaimaiGenreEntryEntity>)

    @Query("DELETE FROM maimai_genre")
    suspend fun deleteAllMaimaiGenres()

    @Query("SELECT COUNT(*) FROM maimai_genre")
    suspend fun getMaimaiGenreCount(): Int

    // Maimai version
    @Query("SELECT * FROM maimai_version ORDER BY id")
    fun getAllMaimaiVersions(): Flow<List<MaimaiVersionEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMaimaiVersions(entries: List<MaimaiVersionEntryEntity>)

    @Query("DELETE FROM maimai_version")
    suspend fun deleteAllMaimaiVersions()

    @Query("SELECT COUNT(*) FROM maimai_version")
    suspend fun getMaimaiVersionCount(): Int
}

@Database(
    entities = [
        ChunithmMusicEntryEntity::class,
        MaimaiMusicEntryEntity::class,
        MaimaiGenreEntryEntity::class,
        MaimaiVersionEntryEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
@TypeConverters(ChafenqiTypeConverters::class)
abstract class ChafenqiDatabase : RoomDatabase() {
    abstract fun songListDao(): SongListDao

    companion object {
        @Volatile
        private var INSTANCE: ChafenqiDatabase? = null

        fun getInstance(context: Context): ChafenqiDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ChafenqiDatabase::class.java,
                    "chafenqi_songlist.db",
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
