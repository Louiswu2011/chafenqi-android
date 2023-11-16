package com.nltv.chafenqi.storage.datastore.user

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsStore(private val context: Context) {
    companion object {
        val Context.settingsStore: DataStore<Preferences> by preferencesDataStore(name = "settingsStore")
        private val shouldForwardKey = booleanPreferencesKey("shouldForward")
        private val homeDefaultGameKey = intPreferencesKey("homeDefaultGame")
    }

    var shouldForward: Flow<Boolean> =
        context.settingsStore.data.map { it[shouldForwardKey] ?: false }
        private set
    suspend fun setShouldForward(value: Boolean) {
        context.settingsStore.edit { it[shouldForwardKey] = value }
    }

    var homeDefaultGame: Flow<Int> = context.settingsStore.data.map { it[homeDefaultGameKey] ?: 1 }
        private set
    suspend fun setHomeDefaultGame(value: Int) {
        context.settingsStore.edit { it[homeDefaultGameKey] = value }
    }
}