package com.nltv.chafenqi.util

import com.nltv.chafenqi.networking.CFQServer

object ChartImageGrabber {
    data class ChartInfo(
        var musicId: String = "",
        var diffIndex: Int = 3
    )

    private val REQUEST_URL = "${CFQServer.defaultPath}/api/resource/chunithm/chart/image"
    private val types = listOf("bar", "bg", "chart")

    fun getChartPreviewImageUrls(chartInfo: ChartInfo): List<String> {
        return types.map { type ->
            "$REQUEST_URL?musicId=${chartInfo.musicId}&diff=${chartInfo.diffIndex}&type=${type}"
        }
    }
}