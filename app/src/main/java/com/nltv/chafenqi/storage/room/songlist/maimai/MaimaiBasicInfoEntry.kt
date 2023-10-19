package com.nltv.chafenqi.storage.room.songlist.maimai

import androidx.annotation.Keep
import androidx.room.Entity
import com.beust.klaxon.Json

data class MaimaiBasicInfoEntry(
    val title: String = "",
    val artist: String = "",
    val genre: String = "",
    val bpm: Int = 0,
    @Json(name = "release_date")  val releaseDate: String = "",
    val from: String = "",
    @Json(name = "is_new") val isNew: Boolean = false
)