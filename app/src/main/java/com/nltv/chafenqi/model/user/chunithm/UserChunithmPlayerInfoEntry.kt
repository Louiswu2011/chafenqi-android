package com.nltv.chafenqi.model.user.chunithm

import kotlinx.serialization.Serializable

@Serializable
data class UserChunithmPlayerInfoEntry(
    val timestamp: Long = 0L,
    val nickname: String = "",
    val level: String = "",
    val trophy: String = "",
    val plate: String = "",
    val dan: Int = 0,
    val ribbon: Int = 0,
    val rating: Double = 0.0,
    val maxRating: Double = 0.0,
    val rawOverpower: Double = 0.0,
    val percentOverpower: Double = 0.0,
    val lastPlayedDate: Long = 0L,
    val friendCode: String = "",
    val currentGold: Int = 0,
    val totalGold: Int = 0,
    val playCount: Int = 0,
    val charName: String = "",
    val charUrl: String = "",
    val charRank: String = "",
    val charExp: Double = 0.0,
    val charIllust: String = "",
    val ghostStatue: Int = 0,
    val silverStatue: Int = 0,
    val goldStatue: Int = 0,
    val rainbowStatue: Int = 0,
)
