package com.nltv.chafenqi.model.team

import kotlinx.serialization.Serializable

@Serializable
data class TeamUpdateCoursePayload(
    val courseName: String,
    val courseTrack1: CourseEntry,
    val courseTrack2: CourseEntry,
    val courseTrack3: CourseEntry,
    val courseHealth: Int,
) {
    @Serializable
    data class CourseEntry(
        val musicId: Long,
        val levelIndex: Int,
    )
}
