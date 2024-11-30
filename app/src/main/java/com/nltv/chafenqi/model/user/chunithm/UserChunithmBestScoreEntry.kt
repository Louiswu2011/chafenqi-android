package com.nltv.chafenqi.model.user.chunithm

import kotlinx.serialization.Serializable

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
)
