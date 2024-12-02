package com.nltv.chafenqi.storage.songlist.maimai

import com.nltv.chafenqi.extension.MAIMAI_VERSION_STRINGS
import kotlinx.serialization.Serializable

@Serializable
data class MaimaiBasicInfoEntry(
    val title: String = "",
    val artist: String = "",
    val genre: String = "",
    val bpm: Int = 0,
    val releaseDate: String = "",
    val version: Int = 0,
    val isNew: Boolean = false
) {
    // TODO: Load version list from server
    val from = MAIMAI_VERSION_STRINGS[version]
}