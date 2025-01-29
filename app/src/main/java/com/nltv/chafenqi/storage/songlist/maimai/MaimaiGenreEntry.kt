package com.nltv.chafenqi.storage.songlist.maimai

import kotlinx.serialization.Serializable

@Serializable
data class MaimaiGenreEntry(
    val id: Int,
    val title: String,
    val genre: String,
)
