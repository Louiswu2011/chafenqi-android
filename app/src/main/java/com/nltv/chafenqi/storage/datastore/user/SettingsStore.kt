package com.nltv.chafenqi.storage.datastore.user

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsStore(private val context: Context) {
    companion object {
        val Context.cacheStore: DataStore<Preferences> by preferencesDataStore(name = "settingsStore")
        private val shouldForwardKey = booleanPreferencesKey("shouldForward")
    }

    var shouldForward: Flow<Boolean> = context.cacheStore.data.map { it[shouldForwardKey] ?: false }
    suspend fun setShouldForward(value: Boolean) { context.cacheStore.edit { it[shouldForwardKey] = value } }
}