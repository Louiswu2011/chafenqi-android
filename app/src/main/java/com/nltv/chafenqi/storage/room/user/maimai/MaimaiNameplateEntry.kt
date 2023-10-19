package com.nltv.chafenqi.storage.room.user.maimai

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.beust.klaxon.Json

@Entity(
    tableName = "maimaiNameplateEntries",
    indices = [Index(value = ["name"], unique = true)]
)
data class MaimaiNameplateEntry(
    @PrimaryKey(autoGenerate = true)
    @Json(ignored = true)
    val uid: Int = 0,

    val name: String = "",
    val description: String = "",
    val image: String = "",
    val area: String = "",
    val selected: Int = 0
)
