package com.nltv.chafenqi.storage.persistent

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.nltv.chafenqi.networking.CFQServer
import com.nltv.chafenqi.storage.SettingsStore.Companion.settingsStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

data class CFQPersistentLoaderConfig(
    val name: String,
    val cacheKey: Preferences.Key<String>,
    val versionKey: Preferences.Key<String>,
    val fetcher: suspend () -> String,
    val gameType: Int,
    val resourceTag: String
)

class CFQPersistentLoader {
    companion object {
        val tag = "CFQPersistentLoader"

        suspend inline fun <reified T> loadPersistentData(
            context: Context,
            config: CFQPersistentLoaderConfig,
            shouldValidate: Boolean
        ): List<T> {
            val cacheStore = context.settingsStore

            Log.i(tag, "Loading persistent data for ${config.name}...")
            return loadPersistentData(cacheStore, config, shouldValidate)
        }

        suspend inline fun <reified T> loadPersistentData(
            store: DataStore<Preferences>,
            config: CFQPersistentLoaderConfig,
            shouldValidate: Boolean
        ): List<T> {
            val musicListString = store.data.map { p -> p[config.cacheKey] ?: "" }.first()
            // No cache found, fetch from remote source.
            if (musicListString.isEmpty()) return Json.decodeFromString(config.fetcher())
                ?: emptyList()

            val list: List<T> = Json.decodeFromString(musicListString) ?: emptyList()
            // No validation needed, return list as-is.
            if (!shouldValidate) return list

            return try {
                // Compare local and remote music list version.
                val localVersion = store.data.map { p -> p[config.versionKey] ?: 0 }.first()
                val remoteVersion = CFQServer.statResourceVersion(tag = config.resourceTag)

                Log.i(tag, "Local ${config.name} hash: $localVersion")
                Log.i(tag, "Remote ${config.name} hash: $remoteVersion")

                if (localVersion == remoteVersion) {
                    Log.i(tag, "Local ${config.name} music list is up to date.")
                    return list
                }

                // Update local music list version.
                Log.i(tag, "Updating local music list data for ${config.name}.")
                store.edit { p -> p[config.versionKey] = remoteVersion }

                val remoteString = config.fetcher()
                Json.decodeFromString(remoteString) ?: list
            } catch (e: Exception) {
                // Network error, return empty list
                Log.e(tag, "Error loading remote music list: $e")
                emptyList()
            }
        }
    }
}