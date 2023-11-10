package com.nltv.chafenqi.storage.songlist.maimai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MaimaiBasicInfoEntry(
    val title: String = "",
    val artist: String = "",
    val genre: String = "",
    val bpm: Int = 0,
    @SerialName("release_date") val releaseDate: String = "",
    val from: String = "",
    @SerialName("is_new") val isNew: Boolean = false
)