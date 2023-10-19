package com.nltv.chafenqi.storage.room.user.maimai

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.beust.klaxon.Json

@Entity(
    tableName = "maimaiCharacterEntries",
    indices = [Index(value = ["name", "image"], unique = true)]
)
data class MaimaiCharacterEntry(
    @PrimaryKey(autoGenerate = true)
    @Json(ignored = true)
    val uid: Int = 0,

    val name: String = "",
    val description: String = "",
    val image: String = "",
    val level: String = "",
    val area: String = "",
    val selected: Int = 0
)
