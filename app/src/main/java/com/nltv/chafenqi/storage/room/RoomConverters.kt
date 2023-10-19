package com.nltv.chafenqi.storage.room

import androidx.room.TypeConverter
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import com.nltv.chafenqi.storage.room.songlist.chunithm.ChunithmMusicCharts
import com.nltv.chafenqi.storage.room.songlist.maimai.MaimaiBasicInfoEntry
import com.nltv.chafenqi.storage.room.songlist.maimai.MaimaiChartEntry

class RoomConverters {
    private val parser = Klaxon()

    @TypeConverter
    fun fromStringList(value: List<String>): String = JsonArray(value).toJsonString()

    @TypeConverter
    fun toStringList(value: String): List<String> = parser.parseArray(value) ?: listOf()

    @TypeConverter
    fun fromIntList(value: List<Int>): String = JsonArray(value).toJsonString()

    @TypeConverter
    fun toIntList(value: String): List<Int> = parser.parseArray(value) ?: listOf()

    @TypeConverter
    fun fromDoubleList(value: List<Double>): String = JsonArray(value).toJsonString()

    @TypeConverter
    fun toDoubleList(value: String): List<Double> = parser.parseArray(value) ?: listOf()

    @TypeConverter
    fun fromChart(value: List<MaimaiChartEntry>) = JsonArray(value).toJsonString()

    @TypeConverter
    fun toChart(value: String): List<MaimaiChartEntry> = parser.parseArray(value) ?: listOf()

    @TypeConverter
    fun fromBasicInfo(value: MaimaiBasicInfoEntry) = Klaxon().toJsonString(value)

    @TypeConverter
    fun toBasicInfo(value: String): MaimaiBasicInfoEntry = parser.parse(value) ?: MaimaiBasicInfoEntry()

    @TypeConverter
    fun fromCharts(value: ChunithmMusicCharts): String = Klaxon().toJsonString(value)

    @TypeConverter
    fun toCharts(value: String): ChunithmMusicCharts = parser.parse(value) ?: ChunithmMusicCharts()
}