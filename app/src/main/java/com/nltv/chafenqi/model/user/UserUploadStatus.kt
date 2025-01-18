package com.nltv.chafenqi.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserUploadStatus(
    @SerialName("chu") val chunithm: Int,
    @SerialName("mai") val maimai: Int,
)