package com.nltv.chafenqi.storage.db.entity

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiBasicInfoEntry
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiChartEntry
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiMusicEntry

@Keep
@Entity(tableName = "maimai_music")
data class MaimaiMusicEntryEntity(
    @PrimaryKey
    @ColumnInfo(name = "music_id") val musicId: Int,
    val title: String,
    val type: String,
    val constants: List<Double>,
    val level: List<String>,
    val charts: List<MaimaiChartEntry>,
    @ColumnInfo(name = "cover_id") val coverId: Int,
    @ColumnInfo(name = "basic_info") val basicInfo: MaimaiBasicInfoEntry,
) {
    fun toModel() = MaimaiMusicEntry(
        musicId = musicId,
        title = title,
        type = type,
        constants = constants,
        level = level,
        charts = charts,
        coverId = coverId,
        basicInfo = basicInfo,
    )

    companion object {
        fun fromModel(entry: MaimaiMusicEntry) = MaimaiMusicEntryEntity(
            musicId = entry.musicId,
            title = entry.title,
            type = entry.type,
            constants = entry.constants,
            level = entry.level,
            charts = entry.charts,
            coverId = entry.coverId,
            basicInfo = entry.basicInfo,
        )
    }
}
