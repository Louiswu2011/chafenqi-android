package com.nltv.chafenqi.storage.room.chunithm

data class ChunithmMusicCharts(
    val basic: ChunithmMusicBasicChart = ChunithmMusicBasicChart(),
    val advanced: ChunithmMusicAdvancedChart = ChunithmMusicAdvancedChart(),
    val expert: ChunithmMusicExpertChart = ChunithmMusicExpertChart(),
    val master: ChunithmMusicMasterChart = ChunithmMusicMasterChart(),
    val ultima: ChunithmMusicUltimaChart = ChunithmMusicUltimaChart(),
    val worldsend: ChunithmMusicWEChart = ChunithmMusicWEChart()
)