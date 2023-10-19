package com.nltv.chafenqi.storage.room.user.maimai

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.beust.klaxon.Json

@Entity(
    tableName = "maimaiDeltaEntries"
)
data class MaimaiDeltaEntry(
    @PrimaryKey(autoGenerate = true)
    @Json(ignored = true)
    val uid: Int = 0,

    val rating: Int = 0,
    val playCount: Int = 0,
    val stats: String = "",
    val dxScore: Int = 0,
    val achievement: Double = 0.0,
    val syncPoint: Int = 0,
    val awakening: String = ""
)
