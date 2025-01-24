package com.nltv.chafenqi.model.team

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

@Serializable
data class TeamActivity(
    val id: Int,
    val timestamp: Long,
    val userId: Long,
    val activity: String,
) {
    companion object {
        val sample = TeamActivity(
            id = 1,
            timestamp = Clock.System.now().epochSeconds,
            userId = 1L,
            activity = "LOUISE/ 创建了团队"
        )
    }
}
