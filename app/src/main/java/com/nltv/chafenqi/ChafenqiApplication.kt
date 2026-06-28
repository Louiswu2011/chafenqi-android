package com.nltv.chafenqi

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.nltv.chafenqi.storage.db.ChafenqiDatabase
import com.nltv.chafenqi.storage.repository.SongListRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

val Context.cacheStore: DataStore<Preferences> by preferencesDataStore(name = "cacheStore")
// const val ONE_SIGNAL_APP_ID = "61d8cb1c-6de2-4b50-af87-f419b2d24ece"

class ChafenqiApplication : Application() {

    lateinit var songListRepository: SongListRepository
        private set

    init {
        instance = this
    }

    companion object {
        private var instance: ChafenqiApplication? = null

        fun applicationContext(): Context {
            return requireNotNull(instance).applicationContext
        }

        fun getRepository(): SongListRepository {
            return (requireNotNull(instance) as ChafenqiApplication).songListRepository
        }
    }

    override fun onCreate() {
        super.onCreate()

        val db = ChafenqiDatabase.getInstance(this)
        songListRepository = SongListRepository(db)
        songListRepository.initFromRoom(CoroutineScope(Dispatchers.IO))

        // OneSignal setup
        // OneSignal.Debug.logLevel = LogLevel.INFO
        // OneSignal.initWithContext(this, ONE_SIGNAL_APP_ID)
        CoroutineScope(Dispatchers.IO).launch {
            // OneSignal.Notifications.requestPermission(true)
        }
    }
}