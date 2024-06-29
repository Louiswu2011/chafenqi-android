package com.nltv.chafenqi.data.leaderboard

import kotlinx.serialization.Serializable

@Serializable
data class ChunithmRatingLeaderboardItem(
    val uid: Int = 0,
    val username: String = "",
    val nickname: String = "",
    val rating: Float = 0f
)

@Serializable
data class MaimaiRatingLeaderboardItem(
    val uid: Int = 0,
    val username: String = "",
    val nickname: String = "",
    val rating: Int = 0
)

typealias ChunithmRatingLeaderboard = MutableList<ChunithmRatingLeaderboardItem>
typealias MaimaiRatingLeaderboard = MutableList<MaimaiRatingLeaderboardItem>