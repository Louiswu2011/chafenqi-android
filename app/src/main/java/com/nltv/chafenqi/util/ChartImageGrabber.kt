package com.nltv.chafenqi.util

object ChartImageGrabber {
    data class MusicInfo(
        var identifier: String = "",
        var diffIndex: Int = 3,
        var title: String
    )


    private val difficultyTag = arrayOf("bas", "adv", "exp", "mst", "ult", "end")

    fun getChartImageUrls(musicInfo: MusicInfo): List<String> {
        val title = musicInfo.title
        val diff = difficultyTag[musicInfo.diffIndex]
        val barUrl = "https://chafenqi.nltv.top/api/chunithm/chart?title=${title}&type=bar"
        val bgUrl = "https://chafenqi.nltv.top/api/chunithm/chart?title=${title}&type=bg"
        val chartUrl = "https://chafenqi.nltv.top/api/chunithm/chart?title=${title}&type=${diff}"

        return listOf(bgUrl, barUrl, chartUrl)
    }
}