package com.nltv.chafenqi.storage.datastore.user.maimai

import kotlinx.serialization.Serializable

@Serializable
data class MaimaiDeltaEntry(
    val rating: Int = 0,
    val playCount: Int = 0,
    val stats: String = "",
    val dxScore: Int = 0,
    val achievement: Double = 0.0,
    val syncPoint: Int = 0,
    val awakening: String = "",
    val createdAt: String = "",
    val updatedAt: String = ""
)
