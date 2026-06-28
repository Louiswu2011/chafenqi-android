package com.nltv.chafenqi.storage.db.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiVersionEntry

@Keep
@Entity(tableName = "maimai_version")
data class MaimaiVersionEntryEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val version: Int,
) {
    fun toModel() = MaimaiVersionEntry(id = id, title = title, version = version)

    companion object {
        fun fromModel(entry: MaimaiVersionEntry) = MaimaiVersionEntryEntity(
            id = entry.id,
            title = entry.title,
            version = entry.version,
        )
    }
}
