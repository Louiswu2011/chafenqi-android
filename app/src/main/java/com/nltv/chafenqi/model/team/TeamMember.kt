package com.nltv.chafenqi.model.team

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

@Serializable
data class TeamMember(
    val id: Int,
    val userId: Long,
    val nickname: String,
    val avatar: String,
    val trophy: String,
    val rating: String,
    val joinAt: Long,
    val activityPoints: Long,
    val playCount: Int,
    val lastActivityAt: Long
) {
    companion object {
        val sample = TeamMember(
            id = 1,
            userId = 1L,
            nickname = "LOUISE/",
            joinAt = Clock.System.now().epochSeconds,
            avatar = "https://new.chunithm-net.com/chuni-mobile/html/mobile/img/f9ed64ced3d22730.png",
            trophy = "What's Up? Pop!",
            rating = "17.03",
            activityPoints = 80240L,
            playCount = 215,
            lastActivityAt = Clock.System.now().epochSeconds,
        )
    }
}
