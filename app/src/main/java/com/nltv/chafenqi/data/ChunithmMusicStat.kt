package com.nltv.chafenqi.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChunithmMusicStat(
    val id: Int = 0,
    @SerialName("idx") val musicId: Int = 0,
    @SerialName("diff") val difficulty: Int = 0,
    val totalPlayed: Int = 0,
    val totalFullCombo: Int = 0,
    val totalAllJustice: Int = 0,
    val totalFullChain: Int = 0,
    val totalScore: Double = 0.0,
    val ssspSplit: Int = 0,
    val sssSplit: Int = 0,
    val sspSplit: Int = 0,
    val ssSplit: Int = 0,
    val spSplit: Int = 0,
    val sSplit: Int = 0,
    val otherSplit: Int = 0,
    val highestScore: Double = 0.0,
    val highestPlayer: String = "",
    val highestPlayerNickname: String = "",
    val createdAt: String = "",
    val updatedAt: String = ""
)
