package com.nltv.chafenqi.model.team

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.Locale

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
                TrackRecord("101.0000%", 0),
                TrackRecord("101.0000%", 0),
                TrackRecord("101.0000%", 0),
            ),
            cleared = true,
        )
    }

    fun totalScore(mode: Int): String {
        return when (mode) {
            0 -> { trackRecords.sumOf { it.score.toInt() }.toString() }
            1 -> { String.format(Locale.getDefault(), "%.4f", trackRecords.sumOf { it.score.replace("%", "").toDouble() }) + "%" }
            else -> ""
        }
    }

    @Transient val rawScore: Double = trackRecords.sumOf { it.score.replace("%", "").toDouble() }
}
