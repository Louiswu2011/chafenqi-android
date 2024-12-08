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
    val remarks: String,
    val promotable: Boolean,
    val lastMonthActivityPoints: Long,
    val currentActivityPoints: Long,
    @SerialName("courseTrack1") val courseTrack1String: String,
    @SerialName("courseTrack2") val courseTrack2String: String,
    @SerialName("courseTrack3") val courseTrack3String: String,
    val courseHealth: Int,
    val courseLastModifiedAt: Long,
    val pinnedMessageId: Int?,
    val createdAt: Long,
    val lastActivityAt: Long,
) {
    companion object {
        val sample = TeamBasicInfo(
            id = 1,
            displayName = "HBK Team",
            nameLastModifiedAt = 1655975168,
            teamCode = "5VQ2d3Pr",
            leaderUserId = 1,
            remarks = "好崩溃战队",
            promotable = true,
            lastMonthActivityPoints = 1000,
            currentActivityPoints = 2000,
            courseTrack1String = "834,4",
            courseTrack2String = "799,4",
            courseTrack3String = "11663,4",
            courseHealth = 100,
            courseLastModifiedAt = 1655975168,
            pinnedMessageId = null,
            createdAt = 1655975168,
            lastActivityAt = 1655975168,
        )

        val empty = TeamBasicInfo(
            id = 0,
            displayName = "",
            nameLastModifiedAt = 0,
            teamCode = "",
            leaderUserId = 0,
            remarks = "",
            promotable = false,
            lastMonthActivityPoints = 0,
            currentActivityPoints = 0,
            courseTrack1String = "0,0",
            courseTrack2String = "0,0",
            courseTrack3String = "0,0",
            courseHealth = 0,
            courseLastModifiedAt = 0,
            pinnedMessageId = null,
            createdAt = 0,
            lastActivityAt = 0,
        )
    }

    class CourseTrack(
        val musicId: Long,
        val levelIndex: Int,
    ) {
        constructor(jsonString: String) : this(
            musicId = jsonString.split(",").first().toLong(),
            levelIndex = jsonString.split(",").last().toInt()
        )
    }

    @Transient val courseTrack1 = CourseTrack(courseTrack1String)
    @Transient val courseTrack2 = CourseTrack(courseTrack2String)
    @Transient val courseTrack3 = CourseTrack(courseTrack3String)

    @Transient val activityDays =
        Instant
            .fromEpochSeconds(createdAt)
            .daysUntil(other = Clock.System.now(), timeZone = TimeZone.currentSystemDefault())
            .absoluteValue
}
