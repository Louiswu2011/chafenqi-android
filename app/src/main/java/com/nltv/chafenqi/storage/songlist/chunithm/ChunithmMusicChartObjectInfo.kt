package com.nltv.chafenqi.storage.songlist.chunithm

import kotlinx.serialization.Serializable

@Serializable
data class ChunithmMusicChartObjectInfo(
    val tap: Int = 0,
    val hold: Int = 0,
    val slide: Int = 0,
    val air: Int = 0,
    val flick: Int = 0,
    val total: Int = 0
)
