package com.nltv.chafenqi.storage

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.nltv.chafenqi.storage.SettingsStore.Companion.settingsStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class IntPreferenceStoreItem(
    val name: String,
    private val defaultValue: Int
) {
    private val key = intPreferencesKey(name)

    fun getValue(context: Context): Flow<Int> {
        return context.settingsStore.data.map { it[key] ?: defaultValue }
    }

    suspend fun setValue(value: Int, context: Context) {
        context.settingsStore.edit { it[key] = value }
    }
}

class BooleanPreferenceStoreItem(
    val name: String,
    private val defaultValue: Boolean
) {
    private val key = booleanPreferencesKey(name)

    fun getValue(context: Context): Flow<Boolean> {
        return context.settingsStore.data.map { it[key] ?: defaultValue }
    }

    suspend fun setValue(value: Boolean, context: Context) {
        context.settingsStore.edit { it[key] = value }
    }
}