package com.nltv.chafenqi.storage.datastore.user.maimai

import kotlinx.serialization.Serializable

@Serializable
data class MaimaiFrameEntry(
    val name: String = "",
    val description: String = "",
    val image: String = "",
    val area: String = "",
    val selected: Int = 0,
    val createdAt: String = "",
    val updatedAt: String = ""
)
