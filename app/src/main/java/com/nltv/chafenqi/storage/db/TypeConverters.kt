package com.nltv.chafenqi.storage.db

import androidx.room.TypeConverter
import com.nltv.chafenqi.storage.songlist.chunithm.ChunithmMusicCharts
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiBasicInfoEntry
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiChartEntry
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ChafenqiTypeConverters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromChunithmCharts(value: ChunithmMusicCharts): String =
        json.encodeToString(value)

    @TypeConverter
    fun toChunithmCharts(value: String): ChunithmMusicCharts =
        json.decodeFromString(value)

    @TypeConverter
    fun fromMaimaiChartList(value: List<MaimaiChartEntry>): String =
        json.encodeToString(value)

    @TypeConverter
    fun toMaimaiChartList(value: String): List<MaimaiChartEntry> =
        json.decodeFromString(value)

    @TypeConverter
    fun fromMaimaiBasicInfo(value: MaimaiBasicInfoEntry): String =
        json.encodeToString(value)

    @TypeConverter
    fun toMaimaiBasicInfo(value: String): MaimaiBasicInfoEntry =
        json.decodeFromString(value)

    @TypeConverter
    fun fromDoubleList(value: List<Double>): String =
        json.encodeToString(value)

    @TypeConverter
    fun toDoubleList(value: String): List<Double> =
        json.decodeFromString(value)

    @TypeConverter
    fun fromStringList(value: List<String>): String =
        json.encodeToString(value)

    @TypeConverter
    fun toStringList(value: String): List<String> =
        json.decodeFromString(value)
}
