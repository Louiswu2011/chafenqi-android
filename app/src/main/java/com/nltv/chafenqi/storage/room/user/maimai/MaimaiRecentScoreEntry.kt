package com.nltv.chafenqi.storage.room.user.maimai

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.beust.klaxon.Json

@Entity(
    tableName = "maimaiRecentScore",
    indices = [Index(value = ["timestamp"], unique = true)]
)
data class MaimaiRecentScoreEntry(
    @PrimaryKey(autoGenerate = true)
    @Json(ignored = true)
    val uid: Int = 0,

    val timestamp: Int = 0,
    val title: String = "",
    val difficulty: String = "",
    val type: String = "",
    val achievements: Float = 0f,
    val isNewRecord: Boolean = false,
    val dxScore: Int = 0,
    val fc: String = "",
    val fs: String = "",
    @Json(name = "notes_tap") val notesTap: String = "",
    @Json(name = "notes_hold") val notesHold: String = "",
    @Json(name = "notes_slide") val notesSlide: String = "",
    @Json(name = "notes_touch") val notesTouch: String = "",
    @Json(name = "notes_break") val notesBreak: String = "",
    val maxCombo: String = "",
    val maxSync: String = "",
    @Json(name = "matching_1") val matching1: String = "",
    @Json(name = "matching_2") val matching2: String = "",
    @Json(name = "matching_3") val matching3: String = ""
)
