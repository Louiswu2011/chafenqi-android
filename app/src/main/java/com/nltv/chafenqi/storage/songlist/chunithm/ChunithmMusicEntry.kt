package com.nltv.chafenqi.storage.songlist.chunithm

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ChunithmMusicEntry(
    val musicID: Int = 0,
    val title: String = "",
    val artist: String = "",
    val genre: String = "",
    val bpm: Int = 0,
    val from: String = "",
    val charts: ChunithmMusicCharts = ChunithmMusicCharts()
)