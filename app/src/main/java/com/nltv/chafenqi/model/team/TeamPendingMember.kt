package com.nltv.chafenqi.model.team

import kotlinx.serialization.Serializable

@Serializable
data class TeamPendingMember(
    val id: Int,
    val userId: Long,
    val timestamp: Long,
    val status: String,
    val message: String,
)
