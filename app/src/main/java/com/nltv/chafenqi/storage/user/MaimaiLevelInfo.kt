package com.nltv.chafenqi.storage.user

import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiBestScoreEntry
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiMusicEntry

data class MaimaiLevelInfo(
    val levelString: String = "",
    val levelIndex: Int = 0,
    val playedBestEntries: List<MaimaiBestScoreEntry> = listOf(),
    val notPlayedMusicEntries: List<MaimaiMusicEntry> = listOf(),
    val musicCount: Int = 0,
    val entryPerRate: List<Int> = listOf()
)
