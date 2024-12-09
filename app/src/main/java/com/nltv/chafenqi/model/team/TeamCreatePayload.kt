package com.nltv.chafenqi.model.team

import kotlinx.serialization.Serializable

@Serializable
data class TeamCreatePayload(
    val game: Int,
    val displayName: String,
    val remarks: String,
    val promotable: Boolean,
)