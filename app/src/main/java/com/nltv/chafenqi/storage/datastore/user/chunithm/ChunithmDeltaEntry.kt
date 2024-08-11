package com.nltv.chafenqi.storage.datastore.user.chunithm

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChunithmDeltaEntry(
    val rating: Double = 0.0,
    @SerialName("overpower_raw") val rawOverpower: Double = 0.0,
    @SerialName("overpower_percent") val overpowerPercentage: Double = 0.0,
    val playCount: Int = 0,
    val totalGold: Int = 0,
    val currentGold: Int = 0,
    val updatedAt: String = "",
    val createdAt: String = "",
)
