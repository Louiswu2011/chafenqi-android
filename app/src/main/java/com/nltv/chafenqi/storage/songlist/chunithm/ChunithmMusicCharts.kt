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
        ultima.constant,
        worldsend.weConstant.toDouble()
    )

    val levels = listOf(
        basic.level,
        advanced.level,
        expert.level,
        master.level,
        ultima.level
    )

    val charters = listOf(
        basic.charter,
        advanced.charter,
        expert.charter,
        master.charter,
        ultima.charter,
        worldsend.charter
    )

    val indexedList = listOf(
        basic, advanced, expert, master, ultima, worldsend
    )

    val hasUltima = ultima.enabled
    val isWE = worldsend.enabled
}