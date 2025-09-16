package com.nltv.chafenqi.model.team

import kotlin.time.Clock
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

@Serializable
data class TeamBulletinBoardEntry(
    val id: Int,
    val timestamp: Long,
    val userId: Long,
    val content: String,
) {
    companion object {
        @OptIn(ExperimentalTime::class)
        val sample = TeamBulletinBoardEntry(
            id = 1,
            timestamp = Clock.System.now().epochSeconds,
            userId = 1,
            content = "好崩溃......"
        )
    }
}
