package com.nltv.chafenqi.storage.datastore.user.chunithm

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChunithmUserInfo(
    val uid: Int = 0,
    val nickname: String = "",
    val trophy: String = "",
    val plate: String = "",
    val dan: Int = 0,
    val ribbon: Int = 0,
    val rating: Float = 0f,
    val maxRating: Float = 0f,
    @SerialName("overpower_raw") val rawOverpower: Float = 0f,
    @SerialName("overpower_percent") val overpowerPercentage: Float = 0f,
    val lastPlayDate: Int = 0,
    val charUrl: String = "",
    val friendCode: String = "",
    val currentGold: Int = 0,
    val totalGold: Int = 0,
    val playCount: Int = 0,
    val createdAt: String = "",
    val updatedAt: String = ""
)
