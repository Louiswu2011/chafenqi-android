package com.nltv.chafenqi.storage.room.songlist.chunithm

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nltv.chafenqi.storage.room.RoomConverters

@Database(entities = [ChunithmMusicEntry::class], version = 2, exportSchema = false)
@TypeConverters(RoomConverters::class)
abstract class ChunithmMusicListDatabase: RoomDatabase() {
    abstract fun ChunithmMusicListDao(): ChunithmMusicListDao

    companion object {
        @Volatile
        private var Instance: ChunithmMusicListDatabase? = null

        fun getDatabase(context: Context): ChunithmMusicListDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, ChunithmMusicListDatabase::class.java, "chuMusicListDb")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}