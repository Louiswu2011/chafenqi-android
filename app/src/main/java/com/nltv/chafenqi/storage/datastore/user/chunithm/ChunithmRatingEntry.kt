package com.nltv.chafenqi.storage.datastore.user.chunithm

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChunithmRatingEntry(
    val idx: String = "",
    val title: String = "",
    @SerialName("level_index") val levelIndex: Int = 0,
    @SerialName("highscore") val score: Int = 0,
    val type: String = "",
    val updatedAt: String = "",
    val createdAt: String = ""
)
