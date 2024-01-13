package com.nltv.chafenqi.storage.persistent

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.nltv.chafenqi.storage.SettingsStore.Companion.settingsStore
import com.nltv.chafenqi.storage.songlist.MusicEntry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

data class CFQPersistentLoaderConfig(
    val name: String,
    val cacheKey: Preferences.Key<String>,
    val fetcher: suspend () -> String
)

class CFQPersistentLoader {
    companion object {
        val tag = "CFQPersistentLoader"

        suspend inline fun <reified T: MusicEntry> loadPersistentData(
            context: Context,
            config: CFQPersistentLoaderConfig,
            shouldValidate: Boolean
        ): List<T> {
            val cacheStore = context.settingsStore

            Log.i(tag, "Loading persistent data for ${config.name}...")
            return loadPersistentData(cacheStore, config, shouldValidate)
        }

        suspend inline fun <reified T: MusicEntry> loadPersistentData(
            store: DataStore<Preferences>,
            config: CFQPersistentLoaderConfig,
            shouldValidate: Boolean
        ): List<T> {
            val musicListString = store.data.map { p -> p[config.cacheKey] ?: "" }.first()
            // No cache found, fetch from remote source.
            if (musicListString.isEmpty()) return Json.decodeFromString(config.fetcher()) ?: emptyList()

            val list: List<T> = Json.decodeFromString(musicListString) ?: emptyList()
            // No validation needed, return list as-is.
            if (!shouldValidate) return list

            return try {
                val remoteString = config.fetcher()
                // Compare local and remote string, prioritize remote source.
                if (musicListString != remoteString) Json.decodeFromString(remoteString)
                    ?: emptyList() else list
            } catch (e: Exception) {
                // Network error, return cache or empty list
                Log.e(tag, "Error loading remote music list: $e")
                if (musicListString.isNotEmpty()) Json.decodeFromString(musicListString) else emptyList()
            }
        }
    }
}