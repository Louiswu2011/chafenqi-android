package com.nltv.chafenqi.storage.songlist.chunithm

import androidx.annotation.Keep
import com.nltv.chafenqi.storage.songlist.MusicEntry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ChunithmMusicEntry(
    @SerialName("musicID") val musicId: Int = 0,
    val title: String = "",
    val artist: String = "",
    val genre: String = "",
    val bpm: Int = 0,
    val from: String = "",
    val charts: ChunithmMusicCharts = ChunithmMusicCharts()
) : MusicEntry {
    val isWE = charts.worldsend.enabled
}