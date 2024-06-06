package com.nltv.chafenqi.util

import kotlinx.serialization.Serializable

@Serializable
data class AppAnnouncement(
    val deviceType: String,
    val announceType: String,
    val title: String,
    val content: String,
    val issueDate: Long
)