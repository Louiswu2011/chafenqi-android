package com.nltv.chafenqi.storage.songlist.maimai

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.beust.klaxon.Json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class MaimaiMusicEntry(
    val id: String = "",
    val title: String = "",
    val type: String = "",
    @SerialName("ds") val constants: List<Double> = listOf(),
    val level: List<String> = listOf(),
    val charts: List<MaimaiChartEntry> = listOf(),
    @SerialName("cids") val chartIds: List<Int> = listOf(),
    @SerialName("basic_info") val basicInfo: MaimaiBasicInfoEntry = MaimaiBasicInfoEntry()
)
