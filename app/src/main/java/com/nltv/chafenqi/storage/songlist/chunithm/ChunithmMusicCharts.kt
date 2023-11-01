package com.nltv.chafenqi.storage.songlist.chunithm

import kotlinx.serialization.Serializable

@Serializable
data class ChunithmMusicCharts(
    val basic: ChunithmMusicBasicChart = ChunithmMusicBasicChart(),
    val advanced: ChunithmMusicAdvancedChart = ChunithmMusicAdvancedChart(),
    val expert: ChunithmMusicExpertChart = ChunithmMusicExpertChart(),
    val master: ChunithmMusicMasterChart = ChunithmMusicMasterChart(),
    val ultima: ChunithmMusicUltimaChart = ChunithmMusicUltimaChart(),
    val worldsend: ChunithmMusicWEChart = ChunithmMusicWEChart()
) {
    val constants = listOf(
        basic.constant,
        advanced.constant,
        expert.constant,
        master.constant,
        ultima.constant
    )

    val isWE = worldsend.enabled
}