package com.nltv.chafenqi.model.user.chunithm

import kotlinx.serialization.Serializable

@Serializable
data class UserChunithmRatingListEntry(
    val index: Int,
    val musicId: Int,
    val score: Int,
    val levelIndex: Int,
)
