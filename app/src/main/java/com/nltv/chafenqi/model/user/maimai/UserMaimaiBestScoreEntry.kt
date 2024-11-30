package com.nltv.chafenqi.model.user.maimai

import kotlinx.serialization.Serializable

@Serializable
data class UserMaimaiBestScoreEntry(
    val musicId: Int,
    val levelIndex: Int,
    val type: String,
    val achievements: Double,
    val dxScore: Int,
    val judgeStatus: String,
    val syncStatus: String,
    val lastModified: Long,
)
