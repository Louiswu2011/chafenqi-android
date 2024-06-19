package com.nltv.chafenqi.data.leaderboard

import kotlinx.serialization.Serializable

@Serializable
data class ChunithmDiffLeaderboardItem(
    val id: Int = 0,
    val uid: Int = 0,
    val username: String = "",
    val nickname: String = "",
    val highscore: Int = 0,
    val rankIndex: Int = 0,
    val clear: String = "",
    val fullCombo: String = "",
    val fullChain: String = "",
    val createdAt: String = "",
    val updatedAt: String = ""
)

typealias ChunithmDiffLeaderboard = List<ChunithmDiffLeaderboardItem>