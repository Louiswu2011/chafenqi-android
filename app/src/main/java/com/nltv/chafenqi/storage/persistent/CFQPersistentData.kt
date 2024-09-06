package com.nltv.chafenqi.storage.persistent

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.nltv.chafenqi.extension.CHUNITHM_GENRE_STRINGS
import com.nltv.chafenqi.extension.CHUNITHM_VERSION_STRINGS
import com.nltv.chafenqi.extension.MAIMAI_GENRE_STRINGS
import com.nltv.chafenqi.extension.MAIMAI_VERSION_STRINGS
import com.nltv.chafenqi.networking.CFQServer
import com.nltv.chafenqi.storage.SettingsStore.Companion.settingsStore
import com.nltv.chafenqi.storage.songlist.chunithm.ChunithmMusicEntry
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiMusicEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object CFQPersistentData {
    private val maiConfig = CFQPersistentLoaderConfig(
        name = "Maimai",
        cacheKey = stringPreferencesKey("maimaiMusicList"),
        versionKey = intPreferencesKey("maimaiMusicListVersion"),
        fetcher = { CFQServer.apiMaimaiMusicData() },
        gameType = 0
    )
    private val chuConfig = CFQPersistentLoaderConfig(
        name = "Chunithm",
        cacheKey = stringPreferencesKey("chunithmMusicList"),
        versionKey = intPreferencesKey("chunithmMusicListVersion"),
        fetcher = { CFQServer.apiChuithmMusicData() },
        gameType = 1
    )

    object Maimai {
        var musicList = listOf<MaimaiMusicEntry>()
        var version: Int = 0
    }

    object Chunithm {
        var musicList = listOf<ChunithmMusicEntry>()
        var version: Int = 0
    }

    suspend fun loadData(shouldValidate: Boolean = true, context: Context) {
        withContext(Dispatchers.IO) {
            Maimai.musicList =
                CFQPersistentLoader.loadPersistentData(context, maiConfig, shouldValidate)
            Chunithm.musicList =
                CFQPersistentLoader.loadPersistentData(context, chuConfig, shouldValidate)

            Maimai.version = CFQServer.statMusicListVersion(gameType = maiConfig.gameType)
            Chunithm.version = CFQServer.statMusicListVersion(gameType = chuConfig.gameType)

            context.settingsStore.edit {
                it[maiConfig.cacheKey] = Json.encodeToString(Maimai.musicList)
                it[chuConfig.cacheKey] = Json.encodeToString(Chunithm.musicList)
            }

            loadFilterResources()
        }
    }

    suspend fun clearData(context: Context) {
        val store = context.settingsStore
        store.edit { it.clear() }
        Maimai.musicList = listOf()
        Chunithm.musicList = listOf()
    }

    private fun loadFilterResources() {
        MAIMAI_GENRE_STRINGS = Maimai.musicList.map { it.basicInfo.genre }.distinct()
        MAIMAI_VERSION_STRINGS = Maimai.musicList.map { it.basicInfo.from }.distinct()
        CHUNITHM_GENRE_STRINGS = Chunithm.musicList.map { it.genre }.distinct()
        CHUNITHM_VERSION_STRINGS = Chunithm.musicList.map { it.from }.distinct()
        Log.i("CFQPD", "Finished loading filter resources.")
    }
}
