package com.nltv.chafenqi.model.user.chunithm

import kotlinx.serialization.Serializable

@Serializable
data class UserChunithmMapIconEntry(
    val name: String,
    val url: String,
    val current: Boolean,
)
