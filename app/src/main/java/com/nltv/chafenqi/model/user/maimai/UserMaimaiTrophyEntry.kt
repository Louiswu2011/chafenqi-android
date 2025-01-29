package com.nltv.chafenqi.model.user.maimai

import kotlinx.serialization.Serializable

@Serializable
data class UserMaimaiTrophyEntry(
    val name: String,
    val description: String,
    val type: String,
    val current: Boolean,
)
