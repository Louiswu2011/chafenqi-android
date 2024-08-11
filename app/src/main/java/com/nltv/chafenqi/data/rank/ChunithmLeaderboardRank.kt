package com.nltv.chafenqi.data.rank

import kotlinx.serialization.Serializable

@Serializable
data class ChunithmRatingRank(
    var uid: Int = 0,
    var username: String = "",
    var nickname: String = "",
    var rating: Double = 0.0,
    var rank: Int = 0
) : LeaderboardRank

@Serializable
data class ChunithmTotalScoreRank(
    var uid: Int = 0,
    var username: String = "",
    var nickname: String = "",
    var totalScore: Int = 0,
    var rank: Int = 0
) : LeaderboardRank

@Serializable
data class ChunithmTotalPlayedRank(
    var uid: Int = 0,
    var username: String = "",
    var nickname: String = "",
    var totalPlayed: Int = 0,
    var rank: Int = 0
) : LeaderboardRank

@Serializable
data class ChunithmFirstRank(
    var rank: Int = 0,
    var firstCount: Int = 0
) : LeaderboardRank