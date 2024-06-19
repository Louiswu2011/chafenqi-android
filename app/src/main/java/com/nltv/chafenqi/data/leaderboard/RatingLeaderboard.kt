package com.nltv.chafenqi.data.leaderboard

import kotlinx.serialization.Serializable

@Serializable
data class ChunithmRatingLeaderboardItem(
    val uid: Int,
    val nickname: String,
    val rating: Float
)

@Serializable
data class MaimaiRatingLeaderboardItem(
    val uid: Int,
    val nickname: String,
    val rating: Int
)

typealias ChunithmRatingLeaderboard = List<ChunithmRatingLeaderboardItem>
typealias MaimaiRatingLeaderboard = List<MaimaiRatingLeaderboardItem>