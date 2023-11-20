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

        private val loginBiometricEnabledKey = booleanPreferencesKey("loginBiometricEnabled")

        private val uploadShouldForwardKey = booleanPreferencesKey("uploadShouldForward")
        private val uploadShouldAutoJumpKey = booleanPreferencesKey("uploadShouldAutoJump")

        private val homeDefaultGameKey = intPreferencesKey("homeDefaultGame")
        private val homeShowRefreshButtonKey = booleanPreferencesKey("homeShowRefreshButton")
    }

    var loginBiometricEnabled: Flow<Boolean> =
        context.settingsStore.data.map { it[loginBiometricEnabledKey] ?: false }
        private set

    suspend fun setLoginBiometricEnabled(value: Boolean) {
        context.settingsStore.edit { it[loginBiometricEnabledKey] = value }
    }

    var uploadShouldForward: Flow<Boolean> =
        context.settingsStore.data.map { it[uploadShouldForwardKey] ?: false }
        private set

    suspend fun setUploadShouldForward(value: Boolean) {
        context.settingsStore.edit { it[uploadShouldForwardKey] = value }
    }

    var uploadShouldAutoJump: Flow<Boolean> =
        context.settingsStore.data.map { it[uploadShouldAutoJumpKey] ?: false }
        private set

    suspend fun setUploadShouldAutoJump(value: Boolean) {
        context.settingsStore.edit { it[uploadShouldAutoJumpKey] = value }
    }

    var homeDefaultGame: Flow<Int> = context.settingsStore.data.map { it[homeDefaultGameKey] ?: 1 }
        private set

    suspend fun setHomeDefaultGame(value: Int) {
        context.settingsStore.edit { it[homeDefaultGameKey] = value }
    }

    var homeShowRefreshButton: Flow<Boolean> =
        context.settingsStore.data.map { it[homeShowRefreshButtonKey] ?: false }
        private set

    suspend fun setHomeShowRefreshButton(value: Boolean) {
        context.settingsStore.edit { it[homeShowRefreshButtonKey] = value }
    }
}