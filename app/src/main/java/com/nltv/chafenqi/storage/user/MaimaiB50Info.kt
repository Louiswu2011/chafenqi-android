package com.nltv.chafenqi.storage.user

import kotlinx.serialization.Serializable

@Serializable
data class MaimaiB50Info(
    val username: String = "",
    val info: MaimaiB50Detail = MaimaiB50Detail()
)

@Serializable
data class MaimaiB50Detail(
    val rating: Int = 0,
    val newRating: Int = 0,
    val pastRating: Int = 0,
    val nickname: String = "",
    val b35: List<MaimaiB50Entry> = listOf(),
    val b15: List<MaimaiB50Entry> = listOf()
)

@Serializable
data class MaimaiB50Entry(
    val index: Int = 0,
    val title: String = "",
    val level: String = "",
    val achievements: Double = 0.0,
    val constant: Double = 0.0,
    val rating: Int = 0,
    val fc: String = "",
    val diffIndex: Int = 0,
    val musicId: String = ""
)
