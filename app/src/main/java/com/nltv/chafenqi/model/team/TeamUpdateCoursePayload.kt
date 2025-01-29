package com.nltv.chafenqi.model.team

import kotlinx.serialization.Serializable

@Serializable
data class TeamUpdateCoursePayload(
    val courseName: String,
    val courseTrack1: TeamBasicInfo.CourseTrack,
    val courseTrack2: TeamBasicInfo.CourseTrack,
    val courseTrack3: TeamBasicInfo.CourseTrack,
    val courseHealth: Int,
) {

}
