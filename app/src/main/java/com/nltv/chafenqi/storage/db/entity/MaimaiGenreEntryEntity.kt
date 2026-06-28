package com.nltv.chafenqi.storage.db.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiGenreEntry

@Keep
@Entity(tableName = "maimai_genre")
data class MaimaiGenreEntryEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val genre: String,
) {
    fun toModel() = MaimaiGenreEntry(id = id, title = title, genre = genre)

    companion object {
        fun fromModel(entry: MaimaiGenreEntry) = MaimaiGenreEntryEntity(
            id = entry.id,
            title = entry.title,
            genre = entry.genre,
        )
    }
}
