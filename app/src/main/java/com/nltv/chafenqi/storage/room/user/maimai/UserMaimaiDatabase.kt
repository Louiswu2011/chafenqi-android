package com.nltv.chafenqi.storage.room.user.maimai

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        MaimaiBestScoreEntry::class,
        MaimaiRecentScoreEntry::class,
        MaimaiDeltaEntry::class,
        MaimaiAvatarEntry::class,
        MaimaiNameplateEntry::class,
        MaimaiFrameEntry::class,
        MaimaiTrophyEntry::class,
        MaimaiCharacterEntry::class,
        MaimaiPartnerEntry::class
    ],
    version = 1,
    exportSchema = false
)
abstract class UserMaimaiDatabase: RoomDatabase() {
    abstract fun UserMaimaiDataDao(): UserMaimaiDataDao

    companion object {
        @Volatile
        private var Instance: UserMaimaiDatabase? = null

        fun getDatabase(context: Context): UserMaimaiDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, UserMaimaiDatabase::class.java, "userMaiDatabase")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}