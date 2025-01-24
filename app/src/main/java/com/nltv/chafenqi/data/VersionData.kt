package com.nltv.chafenqi.data

import com.nltv.chafenqi.BuildConfig
import kotlinx.serialization.Serializable

@Serializable
data class VersionData(
    val androidVersionCode: String = "",
    val androidBuild: String = ""
) {
    fun isLatest(versionCode: String, buildNumber: Int): Boolean {
        return buildNumber >= (androidBuild.toIntOrNull() ?: 0)
    }

    companion object {
        private val currentVersionString = BuildConfig.VERSION_NAME
        private val currentVersionCode = currentVersionString.split(" ")[0]
        private val currentBuildNumber = currentVersionString.split(" ")[1]
            .removePrefix("(")
            .removeSuffix(")")
            .toInt()

        val current = VersionData(currentVersionCode, currentBuildNumber.toString())
    }
}