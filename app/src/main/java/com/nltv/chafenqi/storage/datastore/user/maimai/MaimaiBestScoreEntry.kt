package com.nltv.chafenqi.storage.datastore.user.maimai

import com.nltv.chafenqi.storage.songlist.maimai.MaimaiMusicEntry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class MaimaiBestScoreEntry(
    val id: Int = 0,
    val title: String = "",
    val level: String = "",
    @SerialName("level_index") val levelIndex: Int = 0,
    val type: String = "",
    val achievements: Float = 0f,
    val dxScore: Int = 0,
    val rate: String = "",
    val fc: String = "",
    val fs: String = "",
    @SerialName("ds") val constant: Float = 0f,
    val idx: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
    @Transient var associatedMusicEntry: MaimaiMusicEntry = MaimaiMusicEntry()
)
