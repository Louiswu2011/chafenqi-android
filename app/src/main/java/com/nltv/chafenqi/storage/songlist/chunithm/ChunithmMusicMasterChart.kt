package com.nltv.chafenqi.storage.songlist.chunithm

import kotlinx.serialization.Serializable

@Serializable
data class ChunithmMusicMasterChart(
    val enabled: Boolean = false,
    val constant: Double = 0.0,
    val level: String = "",
    val charter: String? = ""
)