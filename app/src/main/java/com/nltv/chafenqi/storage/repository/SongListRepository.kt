package com.nltv.chafenqi.storage.repository

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.stringPreferencesKey
import com.nltv.chafenqi.extension.CHUNITHM_GENRE_STRINGS
import com.nltv.chafenqi.extension.CHUNITHM_VERSION_STRINGS
import com.nltv.chafenqi.extension.MAIMAI_GENRE_STRINGS
import com.nltv.chafenqi.extension.MAIMAI_VERSION_STRINGS
import com.nltv.chafenqi.networking.CFQServer
import com.nltv.chafenqi.storage.SettingsStore.Companion.settingsStore
import com.nltv.chafenqi.storage.db.ChafenqiDatabase
import com.nltv.chafenqi.storage.db.entity.ChunithmMusicEntryEntity
import com.nltv.chafenqi.storage.db.entity.MaimaiGenreEntryEntity
import com.nltv.chafenqi.storage.db.entity.MaimaiMusicEntryEntity
import com.nltv.chafenqi.storage.db.entity.MaimaiVersionEntryEntity
import com.nltv.chafenqi.storage.songlist.chunithm.ChunithmMusicEntry
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiGenreEntry
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiMusicEntry
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiVersionEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class SongListRepository(private val db: ChafenqiDatabase) {
    companion object {
        private const val TAG = "SongListRepository"
    }

    private val dao = db.songListDao()
    private val json = Json { ignoreUnknownKeys = true }

    // === StateFlows (list form — for filtering/iteration) ===

    private val _maimaiMusicList = MutableStateFlow<List<MaimaiMusicEntry>>(emptyList())
    val maimaiMusicList: StateFlow<List<MaimaiMusicEntry>> = _maimaiMusicList.asStateFlow()

    private val _maimaiGenreList = MutableStateFlow<List<MaimaiGenreEntry>>(emptyList())
    val maimaiGenreList: StateFlow<List<MaimaiGenreEntry>> = _maimaiGenreList.asStateFlow()

    private val _maimaiVersionList = MutableStateFlow<List<MaimaiVersionEntry>>(emptyList())
    val maimaiVersionList: StateFlow<List<MaimaiVersionEntry>> = _maimaiVersionList.asStateFlow()

    private val _chunithmMusicList = MutableStateFlow<List<ChunithmMusicEntry>>(emptyList())
    val chunithmMusicList: StateFlow<List<ChunithmMusicEntry>> = _chunithmMusicList.asStateFlow()

    // === Map indices (O(1) lookup by musicId) ===

    private val _maimaiMusicMap = MutableStateFlow<Map<Int, MaimaiMusicEntry>>(emptyMap())
    val maimaiMusicMap: StateFlow<Map<Int, MaimaiMusicEntry>> = _maimaiMusicMap.asStateFlow()

    private val _chunithmMusicMap = MutableStateFlow<Map<Int, ChunithmMusicEntry>>(emptyMap())
    val chunithmMusicMap: StateFlow<Map<Int, ChunithmMusicEntry>> = _chunithmMusicMap.asStateFlow()

    // === Version hashes ===

    private val _maimaiVersion = MutableStateFlow("")
    val maimaiVersion: StateFlow<String> = _maimaiVersion.asStateFlow()

    private val _chunithmVersion = MutableStateFlow("")
    val chunithmVersion: StateFlow<String> = _chunithmVersion.asStateFlow()

    // === Filter strings (replaces global var in Values.kt) ===

    private val _maimaiGenreStrings = MutableStateFlow<List<String>>(emptyList())
    val maimaiGenreStrings: StateFlow<List<String>> = _maimaiGenreStrings.asStateFlow()

    private val _maimaiVersionStrings = MutableStateFlow<Map<Int, String>>(emptyMap())
    val maimaiVersionStrings: StateFlow<Map<Int, String>> = _maimaiVersionStrings.asStateFlow()

    private val _chunithmGenreStrings = MutableStateFlow<List<String>>(emptyList())
    val chunithmGenreStrings: StateFlow<List<String>> = _chunithmGenreStrings.asStateFlow()

    private val _chunithmVersionStrings = MutableStateFlow<List<String>>(emptyList())
    val chunithmVersionStrings: StateFlow<List<String>> = _chunithmVersionStrings.asStateFlow()

    // === Initialization ===

    fun initFromRoom(scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            // Observe Room Flow → update memory StateFlows
            launch {
                dao.getAllMaimaiMusic().collect { entities ->
                    val list = entities.map { it.toModel() }
                    _maimaiMusicList.value = list
                    _maimaiMusicMap.value = list.associateBy { it.musicId }
                }
            }
            launch {
                dao.getAllMaimaiGenres().collect { entities ->
                    _maimaiGenreList.value = entities.map { it.toModel() }
                }
            }
            launch {
                dao.getAllMaimaiVersions().collect { entities ->
                    _maimaiVersionList.value = entities.map { it.toModel() }
                }
            }
            launch {
                dao.getAllChunithmMusic().collect { entities ->
                    val list = entities.map { it.toModel() }
                    _chunithmMusicList.value = list
                    _chunithmMusicMap.value = list.associateBy { it.musicId }
                }
            }
        }
    }

    // === Remote fetch + cache ===

    suspend fun refreshFromServer(shouldValidate: Boolean = true, context: Context) {
        // DataStore → Room migration on first run
        migrateFromDataStoreIfNeeded(context)

        if (shouldValidate) {
            // Check if remote version changed
            val remoteMaiVersion = try {
                CFQServer.statResourceVersion(tag = "maimai_song_list")
            } catch (e: Exception) { "" }

            val remoteChuVersion = try {
                CFQServer.statResourceVersion(tag = "chunithm_song_list")
            } catch (e: Exception) { "" }

            if (remoteMaiVersion == _maimaiVersion.value && remoteChuVersion == _chunithmVersion.value) {
                // No update needed — but still load filter resources from cached data
                if (_maimaiMusicList.value.isNotEmpty()) {
                    loadFilterResources()
                }
                return
            }
            if (remoteMaiVersion.isNotEmpty()) _maimaiVersion.value = remoteMaiVersion
            if (remoteChuVersion.isNotEmpty()) _chunithmVersion.value = remoteChuVersion
        }

        // Fetch all data from server
        val maiMusicJson = CFQServer.apiMaimaiMusicData()
        val maiGenreJson = CFQServer.apiMaimaiGenreData()
        val maiVersionJson = CFQServer.apiMaimaiVersionData()
        val chuMusicJson = CFQServer.apiChuithmMusicData()

        val maiMusicList: List<MaimaiMusicEntry> =
            if (maiMusicJson.isNotEmpty()) json.decodeFromString(maiMusicJson) else emptyList()
        val maiGenreList: List<MaimaiGenreEntry> =
            if (maiGenreJson.isNotEmpty()) json.decodeFromString(maiGenreJson) else emptyList()
        val maiVersionList: List<MaimaiVersionEntry> =
            if (maiVersionJson.isNotEmpty()) json.decodeFromString(maiVersionJson) else emptyList()
        val chuMusicList: List<ChunithmMusicEntry> =
            if (chuMusicJson.isNotEmpty()) json.decodeFromString(chuMusicJson) else emptyList()

        // Write to Room (REPLACE strategy — upserts by PK)
        if (maiMusicList.isNotEmpty()) {
            dao.deleteAllMaimaiMusic()
            dao.insertAllMaimaiMusic(maiMusicList.map { MaimaiMusicEntryEntity.fromModel(it) })
        }
        if (maiGenreList.isNotEmpty()) {
            dao.deleteAllMaimaiGenres()
            dao.insertAllMaimaiGenres(maiGenreList.map { MaimaiGenreEntryEntity.fromModel(it) })
        }
        if (maiVersionList.isNotEmpty()) {
            dao.deleteAllMaimaiVersions()
            dao.insertAllMaimaiVersions(maiVersionList.map { MaimaiVersionEntryEntity.fromModel(it) })
        }
        if (chuMusicList.isNotEmpty()) {
            dao.deleteAllChunithmMusic()
            dao.insertAllChunithmMusic(chuMusicList.map { ChunithmMusicEntryEntity.fromModel(it) })
        }

        // Update memory StateFlows (Room Flow will also propagate, but set immediately for sync reads)
        _maimaiMusicList.value = maiMusicList
        _maimaiMusicMap.value = maiMusicList.associateBy { it.musicId }
        _maimaiGenreList.value = maiGenreList
        _maimaiVersionList.value = maiVersionList
        _chunithmMusicList.value = chuMusicList
        _chunithmMusicMap.value = chuMusicList.associateBy { it.musicId }

        loadFilterResources()

        Log.i(TAG, "Song list refreshed: " +
            "mai=${maiMusicList.size}, maiGenre=${maiGenreList.size}, " +
            "maiVer=${maiVersionList.size}, chu=${chuMusicList.size}")
    }

    // === DataStore → Room migration ===

    private suspend fun migrateFromDataStoreIfNeeded(context: Context) {
        try {
            val store = context.settingsStore
            val count = dao.getMaimaiMusicCount()
            if (count > 0) return // Already migrated

            val maiMusicKey = stringPreferencesKey("maimaiMusicList")
            val maiGenreKey = stringPreferencesKey("maimaiGenreList")
            val maiVersionKey = stringPreferencesKey("maimaiVersionList")
            val chuMusicKey = stringPreferencesKey("chunithmMusicList")

            val maiMusicStr = store.data.map { it[maiMusicKey] ?: "" }.first()
            if (maiMusicStr.isNotEmpty()) {
                val list: List<MaimaiMusicEntry> = json.decodeFromString(maiMusicStr)
                dao.insertAllMaimaiMusic(list.map { MaimaiMusicEntryEntity.fromModel(it) })
            }

            val maiGenreStr = store.data.map { it[maiGenreKey] ?: "" }.first()
            if (maiGenreStr.isNotEmpty()) {
                val list: List<MaimaiGenreEntry> = json.decodeFromString(maiGenreStr)
                dao.insertAllMaimaiGenres(list.map { MaimaiGenreEntryEntity.fromModel(it) })
            }

            val maiVerStr = store.data.map { it[maiVersionKey] ?: "" }.first()
            if (maiVerStr.isNotEmpty()) {
                val list: List<MaimaiVersionEntry> = json.decodeFromString(maiVerStr)
                dao.insertAllMaimaiVersions(list.map { MaimaiVersionEntryEntity.fromModel(it) })
            }

            val chuMusicStr = store.data.map { it[chuMusicKey] ?: "" }.first()
            if (chuMusicStr.isNotEmpty()) {
                val list: List<ChunithmMusicEntry> = json.decodeFromString(chuMusicStr)
                dao.insertAllChunithmMusic(list.map { ChunithmMusicEntryEntity.fromModel(it) })
            }

            Log.i(TAG, "DataStore → Room migration complete")
        } catch (e: Exception) {
            Log.w(TAG, "DataStore migration failed, will fetch from server: $e")
        }
    }

    // === Filter resources (populates global extension constants) ===

    private fun loadFilterResources() {
        _maimaiGenreStrings.value = _maimaiGenreList.value.map { it.genre }.distinct()
        _maimaiVersionStrings.value = _maimaiVersionList.value.associate { it.version to it.title }
        _chunithmGenreStrings.value = _chunithmMusicList.value.map { it.genre }.distinct()
        _chunithmVersionStrings.value = _chunithmMusicList.value.map { it.from }.distinct()

        // Backward compat: keep global vars populated
        MAIMAI_GENRE_STRINGS = _maimaiGenreStrings.value
        MAIMAI_VERSION_STRINGS = _maimaiVersionStrings.value
        CHUNITHM_GENRE_STRINGS = _chunithmGenreStrings.value
        CHUNITHM_VERSION_STRINGS = _chunithmVersionStrings.value
        Log.i(TAG, "Filter resources loaded")
    }

    // === O(1) lookup helpers ===

    fun getMaimaiEntry(musicId: Int): MaimaiMusicEntry? =
        _maimaiMusicMap.value[musicId]

    fun getChunithmEntry(musicId: Int): ChunithmMusicEntry? =
        _chunithmMusicMap.value[musicId]

    // === Clear ===

    suspend fun clearAll() {
        dao.deleteAllMaimaiMusic()
        dao.deleteAllMaimaiGenres()
        dao.deleteAllMaimaiVersions()
        dao.deleteAllChunithmMusic()
        _maimaiMusicList.value = emptyList()
        _maimaiMusicMap.value = emptyMap()
        _maimaiGenreList.value = emptyList()
        _maimaiVersionList.value = emptyList()
        _chunithmMusicList.value = emptyList()
        _chunithmMusicMap.value = emptyMap()
        _maimaiVersion.value = ""
        _chunithmVersion.value = ""
    }
}
