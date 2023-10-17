package com.nltv.chafenqi

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.nltv.chafenqi.storage.room.RoomContainer
import com.nltv.chafenqi.storage.room.RoomDataContainer

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