package com.nltv.chafenqi.model.team

import kotlinx.serialization.Serializable

@Serializable
data class TeamBasicInfo(
    val id: Int,
    val displayName: String,
    val nameLastModifiedAt: Long,
    val teamCode: String,
    val leaderUserId: Long,
    val remarks: String,
    val promotable: Boolean,
    val lastMonthActivityPoints: Long,
    val currentActivityPoints: Long,
    val courseTrack1: String,
    val courseTrack2: String,
    val courseTrack3: String,
    val courseHealth: Int,
    val courseLastModifiedAt: Long,
    val pinnedMessageId: Int?,
    val createdAt: Long,
    val lastActivityAt: Long,
)
