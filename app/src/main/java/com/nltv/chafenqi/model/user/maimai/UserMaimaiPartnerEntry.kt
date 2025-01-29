package com.nltv.chafenqi.model.user.maimai

import kotlinx.serialization.Serializable

@Serializable
data class UserMaimaiPartnerEntry(
    val name: String,
    val url: String,
    val description: String,
    val current: Boolean,
)
