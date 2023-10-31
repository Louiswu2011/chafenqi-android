package com.nltv.chafenqi.storage.datastore.user.chunithm

import kotlinx.serialization.Serializable

@Serializable
data class ChunithmMapIconEntry(
    val name: String = "",
    val url: String = "",
    val current: Int = 0,
    val updatedAt: String = "",
    val createdAt: String = ""
)
