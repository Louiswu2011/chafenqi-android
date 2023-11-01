package com.nltv.chafenqi.storage.datastore.user.maimai

import kotlinx.serialization.Serializable

@Serializable
data class MaimaiUserInfo(
    val id: Int = 0,
    val uid: Int = 0,
    val nickname: String = "",
    val trophy: String = "",
    val rating: Int = 0,
    val maxRating: Int = 0,
    val star: Int = 0,
    val charUrl: String = "",
    val gradeUrl: String = "",
    val playCount: Int = 0,
    val stats: String = "",
    val createdAt: String = "",
    val updatedAt: String = ""
)