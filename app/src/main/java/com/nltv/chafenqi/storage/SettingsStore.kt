package com.nltv.chafenqi.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsStore(private val context: Context) {
    companion object {
        val Context.settingsStore: DataStore<Preferences> by preferencesDataStore(name = "settingsStore")

        private val uploadShouldAutoJumpKey = booleanPreferencesKey("uploadShouldAutoJump")

        private val homeArrangementKey = stringPreferencesKey("homeArrangement")

        private val infoLevelsChunithmDefaultLevelKey =
            intPreferencesKey("infoLevelsChunithmDefaultLevel")
        private val infoLevelsMaimaiDefaultLevelKey =
            intPreferencesKey("infoLevelsMaimaiDefaultLevel")
    }

    var homeArrangement: Flow<String> =
        context.settingsStore.data.map { it[homeArrangementKey] ?: "最近动态|Rating分析|排行榜|出勤记录" }
        private set

    suspend fun setHomeArrangement(value: String) {
        context.settingsStore.edit { it[homeArrangementKey] = value }
    }

    var uploadShouldAutoJump: Flow<Boolean> =
        context.settingsStore.data.map { it[uploadShouldAutoJumpKey] ?: false }
        private set

    suspend fun setUploadShouldAutoJump(value: Boolean) {
        context.settingsStore.edit { it[uploadShouldAutoJumpKey] = value }
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
}
