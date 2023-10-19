package com.nltv.chafenqi.storage.room.songlist.maimai

import androidx.annotation.Keep
import androidx.room.Entity

data class MaimaiChartEntry(
    val notes: List<Int> = listOf(),
    val charter: String = ""
)