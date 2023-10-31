package com.nltv.chafenqi.storage.songlist.chunithm

import kotlinx.serialization.Serializable

@Serializable
data class ChunithmMusicWEChart(
    val enabled: Boolean = false,
    val constant: Int = 0,
    val level: String = "",
    val charter: String? = null,
    val wetype: String? = null,
    val wediff: Int = 0
)