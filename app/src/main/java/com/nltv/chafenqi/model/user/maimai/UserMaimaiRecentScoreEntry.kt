package com.nltv.chafenqi.model.user.maimai

import com.nltv.chafenqi.storage.songlist.maimai.MaimaiMusicEntry
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class UserMaimaiRecentScoreEntry(
    val timestamp: Long,
    val track: Int,
    val musicId: Int,
    val difficulty: String,
    val type: String,
    val achievements: Double,
    val newRecord: Boolean,
    val dxScore: Int,
    val judgeStatus: String,
    val syncStatus: String,
    val noteTap: List<String>,
    val noteHold: List<String>,
    val noteSlide: List<String>,
    val noteTouch: List<String>,
    val noteBreak: List<String>,
    val maxCombo: String,
    val maxSync: String,
    val players: List<String>,
    @Transient var associatedMusicEntry: MaimaiMusicEntry = MaimaiMusicEntry()
) {
    val levelIndex = when (difficulty.lowercase()) {
        "basic" -> 0
        "advanced" -> 1
        "expert" -> 2
        "master" -> 3
        "remaster" -> 4
        else -> 5
    }
}
