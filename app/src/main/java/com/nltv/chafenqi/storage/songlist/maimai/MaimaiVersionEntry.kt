package com.nltv.chafenqi.storage.songlist.maimai

import kotlinx.serialization.Serializable

@Serializable
data class MaimaiVersionEntry(
    val id: Int,
    val title: String,
    val version: Int,
)
