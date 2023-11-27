package com.nltv.chafenqi.storage.user

import com.nltv.chafenqi.storage.datastore.user.chunithm.ChunithmBestScoreEntry
import com.nltv.chafenqi.storage.songlist.chunithm.ChunithmMusicEntry

data class ChunithmLevelInfo(
    val levelString: String = "",
    val levelIndex: Int = 0,
    val playedBestEntries: List<ChunithmBestScoreEntry> = listOf(),
    val notPlayedMusicEntries: List<ChunithmMusicEntry> = listOf(),
    val musicCount: Int = 0,
    val entryPerRate: List<Int> = listOf()
)
