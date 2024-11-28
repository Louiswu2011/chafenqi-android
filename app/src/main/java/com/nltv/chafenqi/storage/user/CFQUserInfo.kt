package com.nltv.chafenqi.storage.user

import kotlinx.serialization.Serializable

@Serializable
data class CFQUserInfo(
    val id: Long,
    val username: String,
    val password: String,
    val premiumUntil: Long,
    val bindQQ: String,
    val createdAt: Long,
    val lastLogin: Long
)
