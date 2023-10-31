package com.nltv.chafenqi.storage.datastore.user.chunithm

import kotlinx.serialization.SerialInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ChunithmRecentScoreEntry(
    val timestamp: Int = 0,
    val idx: String = "",
    val title: String = "",
    val difficulty: String = "",
    @SerialName("highscore") val score: Int = 0,
    val isNewRecord: Int = 0,
    val clear: String = "",
    @SerialName("full_combo") val fullCombo: String = "",
    @SerialName("full_chain") val fullChain: String = "",
    @SerialName("rank_index") val rankIndex: Int = -1,

    @Transient var judges: Map<String, Int> = mapOf(),
    @SerialName("judges_critical") val judgesCritical: Int = 0,
    @SerialName("judges_justice") val judgesJustice: Int = 0,
    @SerialName("judges_attack") val judgesAttack: Int = 0,
    @SerialName("judges_miss") val judgesMiss: Int = 0,

    @Transient var notes: Map<String, String> = mapOf(),
    @SerialName("notes_tap") val notesTap: String = "",
    @SerialName("notes_hold") val notesHold: String = "",
    @SerialName("notes_slide") val notesSlide: String = "",
    @SerialName("notes_air") val notesAir: String = "",
    @SerialName("notes_flick") val notesFlick: String = "",
    val updatedAt: String = "",
    val createdAt: String = ""
) {
    init {
        judges = mapOf(
            "critical" to judgesCritical,
            "justice" to judgesJustice,
            "attack" to judgesAttack,
            "miss" to judgesMiss
        )
        notes = mapOf(
            "tap" to notesTap,
            "hold" to notesHold,
            "slide" to notesSlide,
            "air" to notesAir,
            "flick" to notesFlick
        )
    }
}
