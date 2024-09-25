package com.nltv.chafenqi.storage.songlist.chunithm

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChunithmMusicUltimaChart(
    override val enabled: Boolean = false,
    override val constant: Double = 0.0,
    override val level: String = "",
    override val charter: String? = "",
    @SerialName("object_count") override val objectInfo: ChunithmMusicChartObjectInfo = ChunithmMusicChartObjectInfo()
) : ChunithmMusicChart