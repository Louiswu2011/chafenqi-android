package com.nltv.chafenqi.model.user.chunithm

import kotlinx.serialization.Serializable

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
)
