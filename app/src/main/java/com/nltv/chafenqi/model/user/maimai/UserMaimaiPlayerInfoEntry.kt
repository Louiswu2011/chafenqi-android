package com.nltv.chafenqi.model.user.maimai

import kotlinx.serialization.Serializable

@Serializable
data class UserMaimaiPlayerInfoEntry(
    val timestamp: Long,
    val nickname: String,
    val trophy: String,
    val rating: Int,
    val maxRating: Int,
    val stars: Int,
    val charUrl: String,
    val gradeUrl: String,
    val playCount: Int,
    val stats: String,
)
