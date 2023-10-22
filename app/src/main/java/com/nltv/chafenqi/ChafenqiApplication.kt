package com.nltv.chafenqi

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.nltv.chafenqi.storage.room.RoomContainer
import com.nltv.chafenqi.storage.room.RoomDataContainer

val Context.cacheStore: DataStore<Preferences> by preferencesDataStore(name = "cacheStore")

class ChafenqiApplication: Application() {
    lateinit var container: RoomContainer

    override fun onCreate() {
        super.onCreate()
        container = RoomDataContainer(this)
        if (!::container.isInitialized) {
            println("Successfully initialized room data container.")
        }
    }
}