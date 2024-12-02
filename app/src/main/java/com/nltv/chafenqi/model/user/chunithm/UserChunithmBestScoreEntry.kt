package com.nltv.chafenqi.model.user.chunithm

import com.nltv.chafenqi.storage.songlist.chunithm.ChunithmMusicEntry
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class UserChunithmBestScoreEntry(
    val musicId: Int,
    val levelIndex: Int,
    val score: Int,
    val rankIndex: Int,
    val clearStatus: String,
    val judgeStatus: String,
    val chainStatus: String,
    val lastModified: Long,
    @Transient var associatedMusicEntry: ChunithmMusicEntry = ChunithmMusicEntry()
)
