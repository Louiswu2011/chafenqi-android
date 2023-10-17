package com.nltv.chafenqi.storage.room.chunithm

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.beust.klaxon.Json

@Keep
@Entity(tableName = "ChunithmMusicList")
data class ChunithmMusicEntry(
    @PrimaryKey(autoGenerate = true)
    @Json(ignored = true)
    val uid: Int = 0,

    val musicID: Int = 0,
    val title: String = "",
    val artist: String = "",
    val genre: String = "",
    val bpm: Int = 0,
    val from: String = "",
    val charts: ChunithmMusicCharts = ChunithmMusicCharts()
)