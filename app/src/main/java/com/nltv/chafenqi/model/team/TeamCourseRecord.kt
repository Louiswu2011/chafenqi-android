package com.nltv.chafenqi.model.team

import kotlinx.serialization.Serializable

@Serializable
data class TeamCourseRecord(
    val id: Int,
    val timestamp: Long,
    val userId: Long,
    val trackRecords: List<TrackRecord>,
    val cleared: Boolean,
) {
    @Serializable
    data class TrackRecord(
        val score: String,
        val damage: Int,
    )
}
