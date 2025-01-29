package com.nltv.chafenqi.model.user.chunithm

import kotlinx.serialization.Serializable

@Serializable
data class UserChunithmSkillEntry(
    val name: String,
    val url: String,
    val level: Int,
    val description: String,
    val current: Boolean,
)
