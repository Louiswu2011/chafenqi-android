package com.nltv.chafenqi.storage.user

import kotlinx.serialization.Serializable

@Serializable
data class CFQUserOptions(
    var bindQQ: Int = -1
)
