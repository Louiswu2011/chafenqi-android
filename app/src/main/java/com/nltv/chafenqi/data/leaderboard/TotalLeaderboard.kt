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
    val totalAchievements: Double = 0.0
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

@Serializable
data class ChunithmFirstLeaderboardItem(
    val uid: Int = 0,
    val username: String = "",
    val nickname: String = "",
    val firstCount: Int = 0,
)

@Serializable
data class MaimaiFirstLeaderboardItem(
    val uid: Int = 0,
    val username: String = "",
    val nickname: String = "",
    val firstCount: Int = 0,
)

typealias ChunithmTotalScoreLeaderboard = List<ChunithmTotalScoreLeaderboardItem>
typealias ChunithmTotalPlayedLeaderboard = List<ChunithmTotalPlayedLeaderboardItem>
typealias MaimaiTotalScoreLeaderboard = List<MaimaiTotalScoreLeaderboardItem>
typealias MaimaiTotalPlayedLeaderboard = List<MaimaiTotalPlayedLeaderboardItem>
typealias MaimaiFirstLeaderboard = List<MaimaiFirstLeaderboardItem>
typealias ChunithmFirstLeaderboard = List<ChunithmFirstLeaderboardItem>