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
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiGenreEntry
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiMusicEntry
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiVersionEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object CFQPersistentData {
    private val maiMusicConfig = CFQPersistentLoaderConfig(
        name = "Maimai",
        cacheKey = stringPreferencesKey("maimaiMusicList"),
        versionKey = intPreferencesKey("maimaiMusicListVersion"),
        fetcher = { CFQServer.apiMaimaiMusicData() },
        gameType = 0,
        resourceTag = "maimai_song_list"
    )
    private val maiGenreConfig = CFQPersistentLoaderConfig(
        name = "MaimaiGenre",
        cacheKey = stringPreferencesKey("maimaiGenreList"),
        versionKey = intPreferencesKey("maimaiGenreListVersion"),
        fetcher = { CFQServer.apiMaimaiGenreData() },
        gameType = 0,
        resourceTag = "maimai_genre_list"
    )
    private val maiVersionConfig = CFQPersistentLoaderConfig(
        name = "MaimaiVersion",
        cacheKey = stringPreferencesKey("maimaiVersionList"),
        versionKey = intPreferencesKey("maimaiVersionListVersion"),
        fetcher = { CFQServer.apiMaimaiVersionData() },
        gameType = 0,
        resourceTag = "maimai_version_list"
    )

    private val chuConfig = CFQPersistentLoaderConfig(
        name = "Chunithm",
        cacheKey = stringPreferencesKey("chunithmMusicList"),
        versionKey = intPreferencesKey("chunithmMusicListVersion"),
        fetcher = { CFQServer.apiChuithmMusicData() },
        gameType = 1,
        resourceTag = "chunithm_song_list"
    )

    object Maimai {
        var musicList = listOf<MaimaiMusicEntry>()
        var genreList = listOf<MaimaiGenreEntry>()
        var versionList = listOf<MaimaiVersionEntry>()
        var version: Int = 0
    }

    object Chunithm {
        var musicList = listOf<ChunithmMusicEntry>()
        var version: Int = 0
    }

    suspend fun loadData(shouldValidate: Boolean = true, context: Context) {
        withContext(Dispatchers.IO) {
            Maimai.musicList =
                CFQPersistentLoader.loadPersistentData(context, maiMusicConfig, shouldValidate)
            Maimai.genreList =
                CFQPersistentLoader.loadPersistentData(context, maiGenreConfig, shouldValidate)
            Maimai.versionList =
                CFQPersistentLoader.loadPersistentData(context, maiVersionConfig, shouldValidate)

            Chunithm.musicList =
                CFQPersistentLoader.loadPersistentData(context, chuConfig, shouldValidate)

            Maimai.version = CFQServer.statResourceVersion(tag = maiMusicConfig.resourceTag)
            Chunithm.version = CFQServer.statResourceVersion(tag = chuConfig.resourceTag)

            context.settingsStore.edit {
                it[maiMusicConfig.cacheKey] = Json.encodeToString(Maimai.musicList)
                it[maiGenreConfig.cacheKey] = Json.encodeToString(Maimai.genreList)
                it[maiVersionConfig.cacheKey] = Json.encodeToString(Maimai.versionList)

                it[chuConfig.cacheKey] = Json.encodeToString(Chunithm.musicList)
            }

            loadFilterResources()

            Chunithm.musicList.filterNot { it.isWE }
        }
    }

    suspend fun clearData(context: Context) {
        val store = context.settingsStore
        store.edit { it.clear() }
        Maimai.musicList = listOf()
        Chunithm.musicList = listOf()
    }

    private fun loadFilterResources() {
        MAIMAI_GENRE_STRINGS = Maimai.genreList.map { it.genre }.distinct()
        MAIMAI_VERSION_STRINGS = Maimai.versionList.associate { it.version to it.title }
        CHUNITHM_GENRE_STRINGS = Chunithm.musicList.map { it.genre }.distinct()
        CHUNITHM_VERSION_STRINGS = Chunithm.musicList.map { it.from }.distinct()
        Log.i("CFQPD", "Finished loading filter resources.")
    }
}
