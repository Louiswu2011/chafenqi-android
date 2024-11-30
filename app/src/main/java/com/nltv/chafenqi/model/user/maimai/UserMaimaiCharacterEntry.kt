package com.nltv.chafenqi.model.user.maimai

import kotlinx.serialization.Serializable

@Serializable
data class UserMaimaiCharacterEntry(
    val name: String,
    val url: String,
    val description: String,
    val level: String,
    val area: String,
    val current: Boolean,
)
