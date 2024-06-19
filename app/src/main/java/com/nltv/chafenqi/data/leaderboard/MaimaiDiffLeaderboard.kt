package com.nltv.chafenqi.data.leaderboard

import kotlinx.serialization.Serializable

@Serializable
data class MaimaiDiffLeaderboardItem(
    val id: Int = 0,
    val uid: Int = 0,
    val username: String = "",
    val nickname: String = "",
    val achievements: Double = 0.0,
    val rate: String = "",
    val fullCombo: String = "",
    val fullSync: String = "",
    val createdAt: String = "",
    val updatedAt: String = ""
)

typealias MaimaiDiffLeaderboard = List<MaimaiDiffLeaderboardItem>