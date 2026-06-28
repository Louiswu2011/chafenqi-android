package com.nltv.chafenqi.storage.db.entity

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nltv.chafenqi.storage.songlist.chunithm.ChunithmMusicCharts
import com.nltv.chafenqi.storage.songlist.chunithm.ChunithmMusicEntry

@Keep
@Entity(tableName = "chunithm_music")
data class ChunithmMusicEntryEntity(
    @PrimaryKey
    @ColumnInfo(name = "music_id") val musicId: Int,
    val title: String,
    val artist: String,
    val genre: String,
    val bpm: Int,
    @ColumnInfo(name = "version_from") val from: String,
    val charts: ChunithmMusicCharts,
) {
    fun toModel() = ChunithmMusicEntry(
        musicId = musicId,
        title = title,
        artist = artist,
        genre = genre,
        bpm = bpm,
        from = from,
        charts = charts,
    )

    companion object {
        fun fromModel(entry: ChunithmMusicEntry) = ChunithmMusicEntryEntity(
            musicId = entry.musicId,
            title = entry.title,
            artist = entry.artist,
            genre = entry.genre,
            bpm = entry.bpm,
            from = entry.from,
            charts = entry.charts,
        )
    }
}
