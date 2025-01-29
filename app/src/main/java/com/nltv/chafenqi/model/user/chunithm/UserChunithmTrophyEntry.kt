package com.nltv.chafenqi.model.user.chunithm

import kotlinx.serialization.Serializable

@Serializable
data class UserChunithmTrophyEntry(
    val name: String,
    val type: String,
    val description: String,
)
