package com.nltv.chafenqi.storage.room.user.maimai

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.beust.klaxon.Json

@Entity(
    tableName = "maimaiPartnerEntries",
    indices = [Index(value = ["name"], unique = true)]
)
data class MaimaiPartnerEntry(
    @PrimaryKey(autoGenerate = true)
    @Json(ignored = true)
    val uid: Int = 0,

    val name: String = "",
    val description: String = "",
    val image: String = "",
    val selected: Int = 0
)
