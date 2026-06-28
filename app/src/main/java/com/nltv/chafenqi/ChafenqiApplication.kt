package com.nltv.chafenqi

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.nltv.chafenqi.di.appModule
import com.nltv.chafenqi.di.userModule
import com.nltv.chafenqi.di.viewModelModule
import com.nltv.chafenqi.storage.repository.SongListRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

val Context.cacheStore: DataStore<Preferences> by preferencesDataStore(name = "cacheStore")
// const val ONE_SIGNAL_APP_ID = "61d8cb1c-6de2-4b50-af87-f419b2d24ece"

class ChafenqiApplication : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: ChafenqiApplication? = null

        fun applicationContext(): Context {
            return requireNotNull(instance).applicationContext
        }

        fun getRepository(): SongListRepository {
            return GlobalContext.get().get()
        }
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@ChafenqiApplication)
            modules(appModule, userModule, viewModelModule)
        }

        val repo: SongListRepository = GlobalContext.get().get()
        repo.initFromRoom(CoroutineScope(Dispatchers.IO))

        // OneSignal setup
        // OneSignal.Debug.logLevel = LogLevel.INFO
        // OneSignal.initWithContext(this, ONE_SIGNAL_APP_ID)
        CoroutineScope(Dispatchers.IO).launch {
            // OneSignal.Notifications.requestPermission(true)
        }
    }
}