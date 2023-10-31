package com.nltv.chafenqi.storage.datastore.user.chunithm

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChunithmDeltaEntry(
    val rating: Double,
    @SerialName("overpower_raw") val rawOverpower: Double,
    @SerialName("overpower_percent") val overpowerPercentage: Double,
    val playCount: Int,
    val totalGold: Int,
    val currentGold: Int,
    val updatedAt: String,
    val createdAt: String,
)
