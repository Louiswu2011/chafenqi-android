package com.nltv.chafenqi.storage.room.user.maimai

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.beust.klaxon.Json

@Entity(
    tableName = "maimaiBestScore",
    indices = [Index(value = ["type", "levelIndex", "title"], unique = true)]
)
data class MaimaiBestScoreEntry(
    @PrimaryKey(autoGenerate = true)
    @Json(ignored = true)
    val uid: Int = 0,

    val title: String = "",
    val level: String = "",
    @Json(name = "level_index") val levelIndex: Int = 0,
    val type: String = "",
    val achievements: Float = 0f,
    val dxScore: Int = 0,
    val rate: String = "",
    val fc: String = "",
    val fs: String = "",
    @Json(name = "ds") val constant: Float = 0f,
    val idx: String = ""
)
