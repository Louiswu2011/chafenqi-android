package com.nltv.chafenqi.model.team

import kotlinx.serialization.Serializable

@Serializable
data class TeamBulletinBoardEntry(
    val id: Int,
    val timestamp: Long,
    val userId: Long,
    val content: String,
)
