package com.nltv.chafenqi.data

import kotlinx.serialization.Serializable

@Serializable
data class VersionData(
    val androidFullVersion: String = "",
    val androidBuild: String = ""
) {
    fun isLatest(currentVersion: String, currentBuild: String): Boolean {
        if (androidFullVersion != currentVersion) { return false }
        if (androidBuild != currentBuild) { return false }
        return true
    }
}