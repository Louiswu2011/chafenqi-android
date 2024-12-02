package com.nltv.chafenqi.storage.user

import com.nltv.chafenqi.model.user.chunithm.UserChunithmBestScoreEntry
import com.nltv.chafenqi.storage.songlist.chunithm.ChunithmMusicEntry

data class ChunithmLevelInfo(
    val levelString: String = "",
    val levelIndex: Int = 0,
    val playedBestEntries: List<UserChunithmBestScoreEntry> = listOf(),
    val notPlayedMusicEntries: List<ChunithmMusicEntry> = listOf(),
    val musicCount: Int = 0,
    val entryPerRate: List<Int> = listOf()
)
