package com.nltv.chafenqi.model.team

import kotlinx.serialization.Serializable

@Serializable
data class TeamInfo(
    val info: TeamBasicInfo,
    val members: List<TeamMember>,
    val pendingMembers: List<TeamPendingMember>,
    val activities: List<TeamActivity>,
    val bulletinBoard: List<TeamBulletinBoardEntry>,
    val courseRecords: List<TeamCourseRecord>,
) {
    companion object {
        val empty = TeamInfo(
            info = TeamBasicInfo.empty,
            members = emptyList(),
            pendingMembers = emptyList(),
            activities = emptyList(),
            bulletinBoard = emptyList(),
            courseRecords = emptyList()
        )
    }
}
