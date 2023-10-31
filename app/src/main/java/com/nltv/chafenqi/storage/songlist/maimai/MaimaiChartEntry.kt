package com.nltv.chafenqi.storage.songlist.maimai

import androidx.annotation.Keep
import androidx.room.Entity
import kotlinx.serialization.Serializable

@Serializable
data class MaimaiChartEntry(
    val notes: List<Int> = listOf(),
    val charter: String = ""
)