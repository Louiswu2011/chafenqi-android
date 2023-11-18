package com.nltv.chafenqi.data

import android.util.Log
import kotlinx.serialization.Serializable

@Serializable
data class VersionData(
    val androidVersionCode: String = "",
    val androidBuild: String = ""
) {
    fun isLatest(versionCode: String, buildNumber: Int): Boolean {
        if (versionCode != androidVersionCode) { return false }
        if (buildNumber < androidBuild.toInt()) { return false }
        return true
    }
}