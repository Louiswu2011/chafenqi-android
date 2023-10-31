package com.nltv.chafenqi.storage.datastore.user.chunithm

import kotlinx.serialization.Serializable

@Serializable
data class ChunithmTicketEntry(
    val name: String = "",
    val url: String = "",
    val count: Int = 0,
    val description: String = "",
    val updatedAt: String = "",
    val createdAt: String = ""
)
