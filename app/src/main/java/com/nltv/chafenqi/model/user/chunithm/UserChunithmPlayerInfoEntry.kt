package com.nltv.chafenqi.model.user.chunithm

import kotlinx.serialization.Serializable

@Serializable
data class UserChunithmPlayerInfoEntry(
    val timestamp: Long,
    val nickname: String,
    val level: String,
    val trophy: String,
    val plate: String,
    val dan: Int,
    val ribbon: Int,
    val rating: Double,
    val maxRating: Double,
    val rawOverpower: Double,
    val percentOverpower: Double,
    val lastPlayedDate: Long,
    val friendCode: String,
    val currentGold: Int,
    val totalGold: Int,
    val playCount: Int,
    val charName: String,
    val charUrl: String,
    val charRank: String,
    val charExp: Double,
    val charIllust: String,
    val ghostStatue: Int,
    val silverStatue: Int,
    val goldStatue: Int,
    val rainbowStatue: Int,
)
