package com.nltv.chafenqi.storage.room.songlist.maimai

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.beust.klaxon.Json

@Keep
data class MaimaiMusicEntry(
    val id: String = "",
    val title: String = "",
    val type: String = "",
    @Json(name = "ds") val constants: List<Double> = listOf(),
    val level: List<String> = listOf(),
    val charts: List<MaimaiChartEntry> = listOf(),
    @Json(name = "cids") val chartIds: List<Int> = listOf(),
    @Json(name = "basic_info") val basicInfo: MaimaiBasicInfoEntry = MaimaiBasicInfoEntry()
)
