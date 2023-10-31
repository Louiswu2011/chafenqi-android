package com.nltv.chafenqi.storage.datastore.user.chunithm

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChunithmCollectionEntry(
    val charUrl: String = "",
    val charName: String = "",
    val charRank: String = "",
    val charExp: Double = 0.0,
    @SerialName("charIllust") val charImg: String = "",
    val ghost: Int = 0,
    val silver: Int = 0,
    val gold: Int = 0,
    val updatedAt: String = "",
    val createdAt: String = ""
)
