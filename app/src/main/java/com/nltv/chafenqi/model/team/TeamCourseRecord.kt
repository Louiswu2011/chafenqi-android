package com.nltv.chafenqi.model.team

import kotlinx.datetime.Clock
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

    companion object {
        val sample = TeamCourseRecord(
            id = 1,
            timestamp = Clock.System.now().epochSeconds,
            userId = 1L,
            trackRecords = listOf(
                TrackRecord("1010000", 0),
                TrackRecord("1010000", 0),
                TrackRecord("1010000", 0),
            ),
            cleared = true,
        )
    }
}
