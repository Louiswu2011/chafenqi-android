package com.nltv.chafenqi.model.user.chunithm

import com.nltv.chafenqi.storage.songlist.chunithm.ChunithmMusicEntry
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class UserChunithmRecentScoreEntry(
    val timestamp: Long,
    val musicId: Int,
    val difficulty: String,
    val score: Int,
    val newRecord: Boolean,
    val judgeCritical: Int,
    val judgeJustice: Int,
    val judgeAttack: Int,
    val judgeMiss: Int,
    val noteTap: String,
    val noteHold: String,
    val noteSlide: String,
    val noteAir: String,
    val noteFlick: String,
    val rankIndex: Int,
    val clearStatus: String,
    val judgeStatus: String,
    val chainStatus: String,
    @Transient var associatedMusicEntry: ChunithmMusicEntry = ChunithmMusicEntry()
) {
    val levelIndex = when (difficulty) {
        "basic" -> 0
        "advanced" -> 1
        "expert" -> 2
        "master" -> 3
        "ultima" -> 4
        else -> 5
    }

    val judges = mapOf(
        "Critical" to judgeCritical,
        "Justice" to judgeJustice,
        "Attack" to judgeAttack,
        "Miss" to judgeMiss
    )
    val notes = mapOf(
        "Tap" to noteTap,
        "Hold" to noteHold,
        "Slide" to noteSlide,
        "Air" to noteAir,
        "Flick" to noteFlick
    )
}
