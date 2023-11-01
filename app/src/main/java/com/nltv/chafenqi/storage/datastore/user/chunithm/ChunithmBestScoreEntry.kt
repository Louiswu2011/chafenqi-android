package com.nltv.chafenqi.storage.datastore.user.chunithm

import com.nltv.chafenqi.storage.songlist.maimai.MaimaiMusicEntry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ChunithmBestScoreEntry(
    val title: String = "",
    @SerialName("level_index") val levelIndex: Int = 0,
    @SerialName("highscore") val score: Int = 0,
    @SerialName("rank_index") val rankIndex: Int = -1,
    val clear: String = "",
    @SerialName("full_combo") val fullCombo: String = "",
    @SerialName("full_chain") val fullChain: String = "",
    var idx: String = "",
    val updatedAt: String = "",
    val createdAt: String = "",
)
