package com.nltv.chafenqi.storage.songlist.chunithm

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ChunithmMusicWEChart(
    override val enabled: Boolean = false,
    @Transient override val constant: Double = 0.0,
    @SerialName("constant") val weConstant: Int = 0,
    override val level: String = "",
    override val charter: String? = null,
    @SerialName("wetype") val chartType: String? = null,
    @SerialName("wediff") val starDifficulty: Int = 0,
    @SerialName("object_count") override val objectInfo: ChunithmMusicChartObjectInfo = ChunithmMusicChartObjectInfo()
) : ChunithmMusicChart