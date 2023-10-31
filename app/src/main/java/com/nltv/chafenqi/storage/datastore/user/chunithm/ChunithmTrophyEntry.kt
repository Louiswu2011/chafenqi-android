package com.nltv.chafenqi.storage.datastore.user.chunithm

import kotlinx.serialization.Serializable

@Serializable
data class ChunithmTrophyEntry(
    val name: String = "",
    val type: String = "",
    val description: String = "",
    val updatedAt: String = "",
    val createdAt: String = ""
)
