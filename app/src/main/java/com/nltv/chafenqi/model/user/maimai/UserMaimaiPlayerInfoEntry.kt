package com.nltv.chafenqi.model.user.maimai

import kotlinx.serialization.Serializable

@Serializable
data class UserMaimaiPlayerInfoEntry(
    val timestamp: Long = 0,
    val nickname: String = "",
    val trophy: String = "",
    val rating: Int = 0,
    val maxRating: Int = 0,
    val stars: Int = 0,
    val charUrl: String = "",
    val gradeUrl: String = "",
    val playCount: Int = 0,
    val stats: String = "",
)
