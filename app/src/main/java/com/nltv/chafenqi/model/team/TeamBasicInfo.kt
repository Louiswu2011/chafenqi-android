package com.nltv.chafenqi.model.team

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.absoluteValue

@Serializable
data class TeamBasicInfo(
    val id: Int,
    val displayName: String,
    val nameLastModifiedAt: Long,
    val teamCode: String,
    val leaderUserId: Long,
    val style: String,
    val remarks: String,
    val promotable: Boolean,
    val lastMonthActivityPoints: Long,
    val currentActivityPoints: Long,
    val courseName: String,
    @SerialName("courseTrack1") val courseTrack1String: String,
    @SerialName("courseTrack2") val courseTrack2String: String,
    @SerialName("courseTrack3") val courseTrack3String: String,
    val coursePrimaryErrorPenalty: Int,
    val courseSecondaryErrorPenalty: Int,
    val courseTertiaryErrorPenalty: Int,
    val courseHealth: Int,
    val courseLastModifiedAt: Long,
    val pinnedMessageId: Int?,
    val createdAt: Long,
    val lastActivityAt: Long,
) {
    companion object {
        val empty = TeamBasicInfo(
            id = 0,
            displayName = "",
            nameLastModifiedAt = 0,
            teamCode = "",
            leaderUserId = 0,
            style = "",
            remarks = "",
            promotable = false,
            lastMonthActivityPoints = 0,
            currentActivityPoints = 0,
            courseName = "",
            courseTrack1String = "0,0",
            courseTrack2String = "0,0",
            courseTrack3String = "0,0",
            coursePrimaryErrorPenalty = 0,
            courseSecondaryErrorPenalty = 0,
            courseTertiaryErrorPenalty = 0,
            courseHealth = 0,
            courseLastModifiedAt = 0,
            pinnedMessageId = null,
            createdAt = 0,
            lastActivityAt = 0,
        )
    }

    @Serializable
    class CourseTrack(
        val musicId: Long,
        val levelIndex: Int,
    ) {
        constructor(jsonString: String) : this(
            musicId = jsonString.split(",").first().toLong(),
            levelIndex = jsonString.split(",").last().toInt()
        )
    }

    @Transient val courseTrack1: CourseTrack? = if (courseTrack1String.isEmpty()) null else CourseTrack(courseTrack1String)
    @Transient val courseTrack2: CourseTrack? = if (courseTrack2String.isEmpty()) null else CourseTrack(courseTrack2String)
    @Transient val courseTrack3: CourseTrack? = if (courseTrack3String.isEmpty()) null else CourseTrack(courseTrack3String)

    @Transient val activityDays =
        Instant
            .fromEpochSeconds(createdAt)
            .daysUntil(other = Clock.System.now(), timeZone = TimeZone.currentSystemDefault())
            .absoluteValue

    @Transient val courseTracks = listOf(courseTrack1, courseTrack2, courseTrack3)
}
