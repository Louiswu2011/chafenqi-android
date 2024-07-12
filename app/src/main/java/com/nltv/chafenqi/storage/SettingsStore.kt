package com.nltv.chafenqi.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsStore(private val context: Context) {
    companion object {
        val Context.settingsStore: DataStore<Preferences> by preferencesDataStore(name = "settingsStore")

        private val loginBiometricEnabledKey = booleanPreferencesKey("loginBiometricEnabled")
        private val loginAutoUpdateSongListKey = booleanPreferencesKey("loginAutoUpdateSongList")

        private val uploadShouldForwardKey = booleanPreferencesKey("uploadShouldForward")
        private val uploadShouldAutoJumpKey = booleanPreferencesKey("uploadShouldAutoJump")

        private val homeDefaultGameKey = intPreferencesKey("homeDefaultGame")
        private val homeShowRefreshButtonKey = booleanPreferencesKey("homeShowRefreshButton")
        private val homeArrangementKey = stringPreferencesKey("homeArrangement")

        private val infoLevelsChunithmDefaultLevelKey =
            intPreferencesKey("infoLevelsChunithmDefaultLevel")
        private val infoLevelsMaimaiDefaultLevelKey =
            intPreferencesKey("infoLevelsMaimaiDefaultLevel")

        private val qsInheritBaseSettingsKey = booleanPreferencesKey("qsInheritBaseSettings")
        private val qsCopyToClipboardKey = booleanPreferencesKey("qsCopyToClipboard")
        private val qsCopyTargetGameKey = intPreferencesKey("qsCopyTargetGame")
        private val qsShouldForwardKey = booleanPreferencesKey("qsShouldForward")
        private val qsShouldAutoJumpKey = booleanPreferencesKey("qsShouldAutoJump")
    }

    var homeArrangement: Flow<String> =
        context.settingsStore.data.map { it[homeArrangementKey] ?: "最近动态|Rating分析|排行榜" }
        private set

    suspend fun setHomeArrangement(value: String) {
        context.settingsStore.edit { it[homeArrangementKey] = value }
    }

    var loginBiometricEnabled: Flow<Boolean> =
        context.settingsStore.data.map { it[loginBiometricEnabledKey] ?: false }
        private set

    suspend fun setLoginBiometricEnabled(value: Boolean) {
        context.settingsStore.edit { it[loginBiometricEnabledKey] = value }
    }

    var loginAutoUpdateSongList: Flow<Boolean> =
        context.settingsStore.data.map { it[loginAutoUpdateSongListKey] ?: true }
        private set

    suspend fun setLoginAutoUpdateSongList(value: Boolean) {
        context.settingsStore.edit { it[loginAutoUpdateSongListKey] = value }
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

    var infoLevelsChunithmDefaultLevel: Flow<Int> =
        context.settingsStore.data.map { it[infoLevelsChunithmDefaultLevelKey] ?: 18 }
        private set

    suspend fun setInfoLevelsChunithmDefaultLevel(value: Int) {
        context.settingsStore.edit { it[infoLevelsChunithmDefaultLevelKey] = value }
    }

    var infoLevelsMaimaiDefaultLevel: Flow<Int> =
        context.settingsStore.data.map { it[infoLevelsMaimaiDefaultLevelKey] ?: 18 }
        private set

    suspend fun setInfoLevelsMaimaiDefaultLevel(value: Int) {
        context.settingsStore.edit { it[infoLevelsMaimaiDefaultLevelKey] = value }
    }

    var qsInheritBaseSettings: Flow<Boolean> =
        context.settingsStore.data.map { it[qsInheritBaseSettingsKey] ?: false }
        private set

    suspend fun setQsInheritBaseSettings(value: Boolean) {
        context.settingsStore.edit { it[qsInheritBaseSettingsKey] = value }
    }

    var qsCopyToClipboard: Flow<Boolean> =
        context.settingsStore.data.map { it[qsCopyToClipboardKey] ?: false }
        private set

    suspend fun setQsCopyToClipboard(value: Boolean) {
        context.settingsStore.edit { it[qsCopyToClipboardKey] = value }
    }

    var qsCopyTargetGame: Flow<Int> =
        context.settingsStore.data.map { it[qsCopyTargetGameKey] ?: 1 }
        private set

    suspend fun setQsCopyTargetGameKey(value: Int) {
        context.settingsStore.edit { it[qsCopyTargetGameKey] = value }
    }

    var qsShouldForward: Flow<Boolean> =
        context.settingsStore.data.map { it[qsShouldForwardKey] ?: false }
        private set

    suspend fun setQsShouldForwardKey(value: Boolean) {
        context.settingsStore.edit { it[qsShouldForwardKey] = value }
    }

    var qsShouldAutoJump: Flow<Boolean> =
        context.settingsStore.data.map { it[qsShouldAutoJumpKey] ?: false }
        private set

    suspend fun setQsShouldAutoJumpKey(value: Boolean) {
        context.settingsStore.edit { it[qsShouldAutoJumpKey] = value }
    }
}
