package com.nltv.chafenqi.data.leaderboard

import kotlinx.serialization.Serializable

@Serializable
data class ChunithmTotalScoreLeaderboardItem(
    val uid: Int = 0,
    val username: String = "",
    val nickname: String = "",
    val totalScore: Long = 0
)

@Serializable
data class MaimaiTotalScoreLeaderboardItem(
    val uid: Int = 0,
    val username: String = "",
    val nickname: String = "",
    val totalAchievements: Float = 0f
)

@Serializable
data class ChunithmTotalPlayedLeaderboardItem(
    val uid: Int = 0,
    val username: String = "",
    val nickname: String = "",
    val totalPlayed: Int = 0
)

@Serializable
data class MaimaiTotalPlayedLeaderboardItem(
    val uid: Int = 0,
    val username: String = "",
    val nickname: String = "",
    val totalPlayed: Int = 0
)

typealias ChunithmTotalScoreLeaderboard = MutableList<ChunithmTotalScoreLeaderboardItem>
typealias ChunithmTotalPlayedLeaderboard = MutableList<ChunithmTotalPlayedLeaderboardItem>
typealias MaimaiTotalScoreLeaderboard = MutableList<MaimaiTotalScoreLeaderboardItem>
typealias MaimaiTotalPlayedLeaderboard = MutableList<MaimaiTotalPlayedLeaderboardItem>