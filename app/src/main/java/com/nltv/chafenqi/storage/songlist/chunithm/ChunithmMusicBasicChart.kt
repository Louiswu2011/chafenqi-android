package com.nltv.chafenqi.storage.songlist.chunithm

import kotlinx.serialization.Serializable

@Serializable
data class ChunithmMusicBasicChart(
    override val enabled: Boolean = false,
    override val constant: Double = 0.0,
    override val level: String = "",
    override val charter: String? = ""
): ChunithmMusicChart