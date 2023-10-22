package com.nltv.chafenqi.storage.room.songlist.chunithm

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.beust.klaxon.Json

@Keep
data class ChunithmMusicEntry(
    val musicID: Int = 0,
    val title: String = "",
    val artist: String = "",
    val genre: String = "",
    val bpm: Int = 0,
    val from: String = "",
    val charts: ChunithmMusicCharts = ChunithmMusicCharts()
)