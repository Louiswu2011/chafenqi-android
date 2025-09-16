package com.nltv.chafenqi.model.team

import kotlin.time.Clock
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

@Serializable
data class TeamPendingMember(
    val id: Int,
    val userId: Long,
    val nickname: String,
    val avatar: String,
    val trophy: String,
    val rating: String,
    val timestamp: Long,
    val status: String,
    val message: String,
) {
    companion object {
        @OptIn(ExperimentalTime::class)
        val sample = TeamPendingMember(
            id = 1,
            userId = 1L,
            nickname = "HBK⭐R9KU",
            avatar = "https://new.chunithm-net.com/chuni-mobile/html/mobile/img/f9ed64ced3d22730.png",
            trophy = "恋ひ恋ふ縁",
            rating = "16.98",
            timestamp = Clock.System.now().epochSeconds,
            status = "pending",
            message = "让我访问"
        )
    }
}
