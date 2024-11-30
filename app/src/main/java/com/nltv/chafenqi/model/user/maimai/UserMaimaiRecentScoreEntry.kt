package com.nltv.chafenqi.model.user.maimai

import kotlinx.serialization.Serializable

@Serializable
data class UserMaimaiRecentScoreEntry(
    val timestamp: Long,
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
)
