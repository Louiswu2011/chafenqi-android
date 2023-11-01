package com.nltv.chafenqi.storage.datastore.user.maimai

import kotlinx.serialization.Serializable

@Serializable
data class MaimaiTrophyEntry(
    val name: String = "",
    val description: String = "",
    val type: String = "",
    val selected: Int = 0,
    val createdAt: String = "",
    val updatedAt: String = ""
)
