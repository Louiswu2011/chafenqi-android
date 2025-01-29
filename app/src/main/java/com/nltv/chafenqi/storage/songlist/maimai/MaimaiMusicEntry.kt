package com.nltv.chafenqi.storage.songlist.maimai

import androidx.annotation.Keep
import com.nltv.chafenqi.storage.songlist.MusicEntry
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class MaimaiMusicEntry(
    val musicId: Int = 0,
    val title: String = "",
    val type: String = "",
    val constants: List<Double> = listOf(),
    val level: List<String> = listOf(),
    val charts: List<MaimaiChartEntry> = listOf(),
    val coverId: Int = 0,
    val basicInfo: MaimaiBasicInfoEntry = MaimaiBasicInfoEntry()
) : MusicEntry
