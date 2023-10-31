package com.nltv.chafenqi.storage.datastore.user.chunithm

import kotlinx.serialization.Serializable

@Serializable
data class ChunithmSkillEntry(
    val name: String = "",
    val icon: String = "",
    val level: Int = 0,
    val description: String = "",
    val current: Int = 0,
    val updatedAt: String = "",
    val createdAt: String = ""
)
