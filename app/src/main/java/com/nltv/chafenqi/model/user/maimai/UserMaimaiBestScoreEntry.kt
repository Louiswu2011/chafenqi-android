package com.nltv.chafenqi.model.user.maimai

import com.nltv.chafenqi.extension.toRateString
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiMusicEntry
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class UserMaimaiBestScoreEntry(
    val musicId: Int = 0,
    val levelIndex: Int = 0,
    val type: String = "",
    val achievements: Double = 0.0,
    val dxScore: Int = 0,
    val judgeStatus: String = "",
    val syncStatus: String = "",
    val lastModified: Long = 0L,
    @Transient var associatedMusicEntry: MaimaiMusicEntry = MaimaiMusicEntry()
) {
    val level: String
        get() = associatedMusicEntry.level.getOrElse(levelIndex) { "" }
    val rateString = achievements.toRateString()
}
