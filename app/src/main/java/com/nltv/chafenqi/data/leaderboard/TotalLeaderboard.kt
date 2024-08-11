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

@Serializable
data class ChunithmFirstLeaderboardItem(
    val uid: Int = 0,
    val username: String = "",
    val nickname: String = "",
    val firstCount: Int = 0,
    val firstMusics: List<ChunithmFirstLeaderboardMusicEntry> = emptyList()
)

@Serializable
data class ChunithmFirstLeaderboardMusicEntry(
    val musicId: Int = 0,
    val diffIndex: Int = 0,
    val score: Int = 0
)

@Serializable
data class MaimaiFirstLeaderboardItem(
    val uid: Int = 0,
    val username: String = "",
    val nickname: String = "",
    val firstCount: Int = 0,
    val firstMusics: List<MaimaiFirstLeaderboardMusicEntry> = emptyList()
)

@Serializable
data class MaimaiFirstLeaderboardMusicEntry(
    val musicId: Int = 0,
    val diffIndex: Int = 0,
    val achievements: Double = 0.0
)

typealias ChunithmTotalScoreLeaderboard = List<ChunithmTotalScoreLeaderboardItem>
typealias ChunithmTotalPlayedLeaderboard = List<ChunithmTotalPlayedLeaderboardItem>
typealias MaimaiTotalScoreLeaderboard = List<MaimaiTotalScoreLeaderboardItem>
typealias MaimaiTotalPlayedLeaderboard = List<MaimaiTotalPlayedLeaderboardItem>
typealias MaimaiFirstLeaderboard = List<MaimaiFirstLeaderboardItem>
typealias ChunithmFirstLeaderboard = List<ChunithmFirstLeaderboardItem>