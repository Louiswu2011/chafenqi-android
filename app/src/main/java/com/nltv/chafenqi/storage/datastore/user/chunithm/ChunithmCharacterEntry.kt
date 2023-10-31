package com.nltv.chafenqi.storage.datastore.user.chunithm

import kotlinx.serialization.Serializable

@Serializable
data class ChunithmCharacterEntry(
    val name: String = "",
    val url: String = "",
    val rank: String = "",
    val exp: Double = 0.0,
    val current: Int = 0,
    val updatedAt: String = "",
    val createdAt: String = ""
)
