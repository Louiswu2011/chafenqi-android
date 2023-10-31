package com.nltv.chafenqi.storage.`object`

import android.content.Context
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.beust.klaxon.Klaxon
import com.nltv.chafenqi.ChafenqiApplication
import com.nltv.chafenqi.cacheStore
import com.nltv.chafenqi.networking.CFQServer
import com.nltv.chafenqi.networking.FishServer
import com.nltv.chafenqi.storage.songlist.chunithm.ChunithmMusicEntry
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiMusicEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object CFQPersistentData {
    private const val TAG = "CFQPersistentData"

    private val maiListKey = stringPreferencesKey("maimaiMusicList")
    private val chuListKey = stringPreferencesKey("chunithmMusicList")

    object Maimai {
        var musicList = listOf<MaimaiMusicEntry>()
        suspend fun loadData(shouldValidate: Boolean, context: Context) {
            val cacheStore = context.cacheStore

            Log.i(TAG, "Loading ${this.javaClass.name} music list...")
            loadDataFromCache(shouldValidate, cacheStore)
            if (musicList.isEmpty()) {
                loadDataFromWeb()
                saveCacheData(cacheStore)
            }
        }

        private suspend fun loadDataFromWeb(stringFromValidate: String = "") {
            Log.i(TAG, "Fetching ${this.javaClass.canonicalName} music list from web...")
            val maiListString = stringFromValidate.ifEmpty { FishServer.fetchMaimaiMusicListData() }
            if (maiListString.isNotEmpty()) {
                musicList = Json.decodeFromString(maiListString) ?: listOf()
            }
        }

        private suspend fun loadDataFromCache(shouldValidate: Boolean, cacheStore: DataStore<Preferences>) {
            Log.i(TAG, "Loading ${this.javaClass.name} music list from cache...")
            val maiListString = cacheStore.data.map { p -> p[maiListKey] ?: "" }
                .first()


            if (maiListString.isNotEmpty()) {
                if (shouldValidate) {
                    val onlineMaiListString = FishServer.fetchMaimaiMusicListData()
                    if (maiListString != onlineMaiListString) {
                        loadDataFromWeb()
                        return
                    }
                } else {
                    musicList = Json.decodeFromString(maiListString) ?: listOf()
                }
            }
        }

        private suspend fun saveCacheData(cacheStore: DataStore<Preferences>) {
            Log.i(TAG, "Saving ${this.javaClass.name} music list from cache...")
            cacheStore.edit {
                it[maiListKey] = Json.encodeToString(musicList)
            }
        }
    }

    object Chunithm {
        var musicList = listOf<ChunithmMusicEntry>()

        suspend fun loadData(shouldValidate: Boolean, context: Context) {
            val cacheStore = context.cacheStore

            Log.i(TAG, "Loading ${this.javaClass.name} music list...")
            loadDataFromCache(shouldValidate, cacheStore)
            if (musicList.isEmpty()) {
                loadDataFromWeb()
                saveCacheData(cacheStore)
            }
        }

        private suspend fun loadDataFromWeb(stringFromValidate: String = "") {
            Log.i(TAG, "Fetching ${this.javaClass.name} music list from web...")
            var chuListString = stringFromValidate.ifEmpty { CFQServer.apiChuithmMusicData() }
            if (chuListString.isNotEmpty()) {
                musicList = Json.decodeFromString(chuListString) ?: listOf()
            }
        }

        private suspend fun loadDataFromCache(shouldValidate: Boolean, cacheStore: DataStore<Preferences>) {
            Log.i(TAG, "Loading ${this.javaClass.name} music list from cache...")
            var chuListString = cacheStore.data.map { p -> p[chuListKey] ?: "" }
                .first()


            if (chuListString.isNotEmpty()) {
                if (shouldValidate) {
                    val onlineChuListString = CFQServer.apiChuithmMusicData()
                    if (chuListString != onlineChuListString) {
                        loadDataFromWeb()
                        return
                    }
                } else {
                    musicList = Json.decodeFromString(chuListString) ?: listOf()
                }
            }
        }

        private suspend fun saveCacheData(cacheStore: DataStore<Preferences>) {
            Log.i(TAG, "Saving ${this.javaClass.name} music list from cache...")
            cacheStore.edit {
                it[chuListKey] = Json.encodeToString(musicList)
            }
        }
    }

    suspend fun loadData(shouldValidate: Boolean = true, context: Context) {
        withContext(Dispatchers.IO) {
            Maimai.loadData(shouldValidate, context)
            Chunithm.loadData(shouldValidate, context)
        }
    }

    suspend fun clearData(context: Context) {
        val store = context.cacheStore
        store.edit { it.clear() }
    }
}
