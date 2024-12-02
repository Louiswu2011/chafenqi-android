package com.nltv.chafenqi.model.user.chunithm

import com.nltv.chafenqi.storage.songlist.chunithm.ChunithmMusicEntry
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class UserChunithmRatingListEntry(
    val index: Int = 0,
    val musicId: Int = 0,
    val score: Int = 0,
    val levelIndex: Int = 0,
    @Transient var associatedMusicEntry: ChunithmMusicEntry = ChunithmMusicEntry()
)
