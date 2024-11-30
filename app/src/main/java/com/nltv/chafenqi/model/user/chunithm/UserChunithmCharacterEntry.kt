package com.nltv.chafenqi.model.user.chunithm

import kotlinx.serialization.Serializable

@Serializable
data class UserChunithmCharacterEntry(
    val name: String,
    val url: String,
    val rank: String,
    val exp: Double,
    val current: Boolean,
)
