package com.nltv.chafenqi.data

import kotlinx.serialization.Serializable

@Serializable
data class VersionData(
    val androidFullVersion: String = "",
    val androidBuild: String = ""
) {
    fun isLatest(currentBuild: String): Boolean {
        if (androidBuild != currentBuild) { return false }
        return true
    }
}