package com.nltv.chafenqi.data.rank

import kotlinx.serialization.Serializable

@Serializable
data class MaimaiRatingRank(
    var uid: Int = 0,
    var username: String = "",
    var nickname: String = "",
    var rating: Int = 0,
    var rank: Int = 0
) : LeaderboardRank

@Serializable
data class MaimaiTotalScoreRank(
    var uid: Int = 0,
    var username: String = "",
    var nickname: String = "",
    var totalAchievements: Double = 0.0,
    var rank: Int = 0
) : LeaderboardRank

@Serializable
data class MaimaiTotalPlayedRank(
    var uid: Int = 0,
    var username: String = "",
    var nickname: String = "",
    var totalPlayed: Int = 0,
    var rank: Int = 0
) : LeaderboardRank

@Serializable
data class MaimaiFirstRank(
    var rank: Int = 0,
    var firstCount: Int = 0
) : LeaderboardRank