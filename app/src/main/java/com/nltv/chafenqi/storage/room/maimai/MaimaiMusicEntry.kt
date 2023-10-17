package com.nltv.chafenqi.storage.room.maimai

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.beust.klaxon.Json

@Keep
@Entity(tableName = "MaimaiMusicList")
data class MaimaiMusicEntry(
    @PrimaryKey(autoGenerate = true)
    @Json(ignored = true)
    val uid: Int = 0,

    val id: String = "",
    val title: String = "",
    val type: String = "",
    @Json(name = "ds") val constants: List<Double> = listOf(),
    val level: List<String> = listOf(),
    val charts: List<MaimaiChartEntry> = listOf(),
    @Json(name = "cids") val chartIds: List<Int> = listOf(),
    @Json(name = "basic_info") val basicInfo: MaimaiBasicInfoEntry = MaimaiBasicInfoEntry()
)
