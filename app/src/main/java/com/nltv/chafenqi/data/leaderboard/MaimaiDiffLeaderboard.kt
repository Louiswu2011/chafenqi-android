package com.nltv.chafenqi.data.leaderboard

import com.nltv.chafenqi.extension.toRateString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MaimaiDiffLeaderboardItem(
    @SerialName("index") val id: Int = 0,
    val uid: Int = 0,
    val username: String = "",
    val nickname: String = "",
    val achievements: Double = 0.0,
    @SerialName("judgeStatus") val fullCombo: String = "",
    @SerialName("syncStatus") val fullSync: String = "",
    val timestamp: Long = 0
) {
    val rate = achievements.toRateString()
}

typealias MaimaiDiffLeaderboard = List<MaimaiDiffLeaderboardItem>