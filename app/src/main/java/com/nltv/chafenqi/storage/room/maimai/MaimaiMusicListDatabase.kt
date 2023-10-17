package com.nltv.chafenqi.storage.room.maimai

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nltv.chafenqi.storage.room.RoomConverters

@Database(entities = [MaimaiMusicEntry::class], version = 1, exportSchema = false)
@TypeConverters(RoomConverters::class)
abstract class MaimaiMusicListDatabase: RoomDatabase() {
    abstract fun MaimaiMusicListDao(): MaimaiMusicListDao

    companion object {
        @Volatile
        private var Instance: MaimaiMusicListDatabase? = null

        fun getDatabase(context: Context): MaimaiMusicListDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, MaimaiMusicListDatabase::class.java, "maiMusicListDb")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}