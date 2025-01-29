package com.nltv.chafenqi.model.team

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

@Serializable
data class TeamBulletinBoardEntry(
    val id: Int,
    val timestamp: Long,
    val userId: Long,
    val content: String,
) {
    companion object {
        val sample = TeamBulletinBoardEntry(
            id = 1,
            timestamp = Clock.System.now().epochSeconds,
            userId = 1,
            content = "好崩溃......"
        )
    }
}
