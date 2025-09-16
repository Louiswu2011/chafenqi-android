package com.nltv.chafenqi.model.team

import kotlin.time.Clock
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

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
        val avatarPool = listOf(
            "https://new.chunithm-net.com/chuni-mobile/html/mobile/img/f9ed64ced3d22730.png",
            "https://new.chunithm-net.com/chuni-mobile/html/mobile/img/144dc4baaf0356a3.png",
            "https://new.chunithm-net.com/chuni-mobile/html/mobile/img/fbea06b0aec2cf63.png",
            "https://new.chunithm-net.com/chuni-mobile/html/mobile/img/271a7686e029bff7.png"
        )

        @OptIn(ExperimentalTime::class)
        val sample = TeamMember(
            id = 1,
            userId = 1L,
            nickname = "LOUISE/",
            joinAt = Clock.System.now().epochSeconds,
            avatar = avatarPool.random(),
            trophy = "What's Up? Pop!",
            rating = "17.03",
            activityPoints = 80240L,
            playCount = 215,
            lastActivityAt = Clock.System.now().epochSeconds,
        )
    }
}
