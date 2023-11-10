package com.nltv.chafenqi.storage.datastore.user.maimai

import com.nltv.chafenqi.storage.datastore.user.RecentScoreEntry
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiMusicEntry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class MaimaiRecentScoreEntry(
    val id: Int = 0,
    override val timestamp: Int = 0,
    val title: String = "",
    val difficulty: String = "",
    val type: String = "",
    val achievements: Float = 0f,
    val isNewRecord: Int = 0,
    val dxScore: Int = 0,
    val fc: String = "",
    val fs: String = "",
    @SerialName("notes_tap") val notesTap: String = "",
    @SerialName("notes_hold") val notesHold: String = "",
    @SerialName("notes_slide") val notesSlide: String = "",
    @SerialName("notes_touch") val notesTouch: String = "",
    @SerialName("notes_break") val notesBreak: String = "",
    val maxCombo: String = "",
    val maxSync: String = "",
    @SerialName("matching_1") val matching1: String = "",
    @SerialName("matching_2") val matching2: String = "",
    @SerialName("matching_3") val matching3: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
    @Transient var associatedMusicEntry: MaimaiMusicEntry = MaimaiMusicEntry()
) : RecentScoreEntry {
    val levelIndex = when (difficulty.lowercase()) {
        "basic" -> 0
        "advanced" -> 1
        "expert" -> 2
        "master" -> 3
        else -> 4
    }
}
