package com.nltv.chafenqi.storage.songlist.chunithm

interface ChunithmMusicChart {
    val enabled: Boolean
    val constant: Double
    val level: String
    val charter: String?
    val objectInfo: ChunithmMusicChartObjectInfo?
}