package com.nltv.chafenqi.data.leaderboard

import kotlinx.serialization.Serializable

@Serializable
data class ChunithmRatingLeaderboardItem(
    val uid: Int = 0,
    val username: String = "",
    val nickname: String = "",
    val rating: Double = 0.0
)

@Serializable
data class MaimaiRatingLeaderboardItem(
    val uid: Int = 0,
    val username: String = "",
    val nickname: String = "",
    val rating: Int = 0
)

typealias ChunithmRatingLeaderboard = List<ChunithmRatingLeaderboardItem>
typealias MaimaiRatingLeaderboard = List<MaimaiRatingLeaderboardItem>