package com.nltv.chafenqi.model.user.chunithm

import kotlinx.serialization.Serializable

@Serializable
data class UserChunithmTicketEntry(
    val name: String,
    val url: String,
    val count: Int,
    val description: String,
)
