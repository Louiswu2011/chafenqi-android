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
        val barUrl = "http://43.139.107.206:8083/api/chunithm/chart?title=${title}&type=bar"
        val bgUrl = "http://43.139.107.206:8083/api/chunithm/chart?title=${title}&type=bg"
        val chartUrl = "http://43.139.107.206:8083/api/chunithm/chart?title=${title}&type=${diff}"

        return listOf(bgUrl, barUrl, chartUrl)
    }
}