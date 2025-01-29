package com.nltv.chafenqi.data.leaderboard

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChunithmDiffLeaderboardItem(
    @SerialName("index") val id: Int = 0,
    val uid: Int = 0,
    val username: String = "",
    val nickname: String = "",
    val score: Int = 0,
    val rankIndex: Int = 0,
    @SerialName("clearStatus") val clear: String = "",
    @SerialName("judgeStatus") val fullCombo: String = "",
    @SerialName("chainStatus") val fullChain: String = "",
    val timestamp: Long = 0
)

typealias ChunithmDiffLeaderboard = List<ChunithmDiffLeaderboardItem>