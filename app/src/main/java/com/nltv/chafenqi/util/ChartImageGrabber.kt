package com.nltv.chafenqi.util

object ChartImageGrabber {
    data class ChartInfo(
        var musicId: String = "",
        var diffIndex: Int = 3
    )

    private const val REQUEST_URL = "https://chafenqi.nltv.top/api/chunithm/preview"
    private val types = listOf("bar", "bg", "chart")

    fun getChartPreviewImageUrls(chartInfo: ChartInfo): List<String> {
        return types.map { type ->
            "$REQUEST_URL?musicId=${chartInfo.musicId}&diff=${chartInfo.diffIndex}&type=${type}"
        }
    }
}