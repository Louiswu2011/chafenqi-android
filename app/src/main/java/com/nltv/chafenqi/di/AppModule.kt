package com.nltv.chafenqi.di

import com.nltv.chafenqi.networking.CFQServer
import com.nltv.chafenqi.networking.CFQTeamServer
import com.nltv.chafenqi.storage.SettingsStore
import com.nltv.chafenqi.storage.db.ChafenqiDatabase
import com.nltv.chafenqi.storage.repository.SongListRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    // Database
    single { ChafenqiDatabase.getInstance(androidContext()) }

    // Network
    single { CFQServer() }
    single { CFQTeamServer }

    // Settings
    single { SettingsStore(androidContext()) }

    // Repository — initFromRoom will be called separately in Application.onCreate
    single { SongListRepository(get()) }
}
