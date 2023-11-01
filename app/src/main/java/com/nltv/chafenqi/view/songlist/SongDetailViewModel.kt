package com.nltv.chafenqi.view.songlist

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.nltv.chafenqi.extension.toMaimaiCoverPath
import com.nltv.chafenqi.storage.CFQUser
import com.nltv.chafenqi.storage.datastore.user.chunithm.ChunithmBestScoreEntry
import com.nltv.chafenqi.storage.datastore.user.chunithm.ChunithmRecentScoreEntry
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiBestScoreEntry
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiRecentScoreEntry
import com.nltv.chafenqi.storage.`object`.CFQPersistentData
import com.nltv.chafenqi.storage.songlist.chunithm.ChunithmMusicEntry
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiMusicEntry

val maimaiDifficultyTitles = listOf("Basic", "Advanced", "Expert", "Master", "Re:Master")
val chunithmDifficultyTitles = listOf("Basic", "Advanced", "Expert", "Master", "Ultima", "World's End")

val maimaiDifficultyColors = listOf(
    Color(red = 128, green = 216, blue = 98),
    Color(red = 242, green = 218, blue = 71),
    Color(red = 237, green = 127, blue = 132),
    Color(red = 176, green = 122, blue = 238),
    Color(red = 206, green = 164, blue = 251),
)
val chunithmDifficultyColors = listOf(
    Color(red = 73, green = 166, blue = 137),
    Color(red = 237, green = 123, blue = 33),
    Color(red = 205, green = 85, blue = 77),
    Color(red = 171, green = 104, blue = 249),
    Color(red = 32, green = 32, blue = 32),
    Color.White
)

class SongDetailViewModel: ViewModel() {
    private val tag = this::class.java.canonicalName

    var maiScores: List<MaimaiBestScoreEntry> = listOf()
    var chuScores: List<ChunithmBestScoreEntry> = listOf()

    var maiMusic: MaimaiMusicEntry? = null
    var chuMusic: ChunithmMusicEntry? = null

    var coverUrl: String = ""
    var title: String = ""
    var artist: String = ""
    var constants: List<String> = listOf()
    var bpm: String = ""
    var version: String = ""
    var genre: String = ""

    val maiDiffInfos: MutableList<MaimaiDifficultyInfo> = mutableListOf()

    fun update(mode: Int, index: Int) {
        if (mode == 0 && CFQPersistentData.Chunithm.musicList.isNotEmpty()) {
            chuMusic = CFQPersistentData.Chunithm.musicList[index]

            coverUrl = "http://43.139.107.206:8083/api/chunithm/cover?musicId=${chuMusic?.musicID}"
            title = chuMusic?.title ?: ""
            artist = chuMusic?.artist ?: ""
            constants = chuMusic?.charts?.constants?.map { String.format("%.1f", it) } ?: listOf()
            bpm = chuMusic?.bpm?.toString() ?: ""
            version = chuMusic?.from ?: ""
            genre = chuMusic?.genre ?: ""
        } else if (mode == 1 && CFQPersistentData.Maimai.musicList.isNotEmpty()) {
            maiMusic = CFQPersistentData.Maimai.musicList[index]

            coverUrl = maiMusic?.id?.toMaimaiCoverPath() ?: ""
            title = maiMusic?.title ?: ""
            artist = maiMusic?.basicInfo?.artist ?: ""
            constants = maiMusic?.constants?.map { String.format("%.1f", it) } ?: listOf()
            bpm = maiMusic?.basicInfo?.bpm?.toString() ?: ""
            version = maiMusic?.basicInfo?.from ?: ""
            genre = maiMusic?.basicInfo?.genre ?: ""

            maiMusic?.charts?.forEachIndexed { difficultyIndex, _ ->
                maiDiffInfos.add(
                    MaimaiDifficultyInfo(
                        title,
                        difficultyIndex,
                        maiMusic ?: MaimaiMusicEntry()
                    )
                )
            }
        }
    }
}

class MaimaiDifficultyInfo(
    title: String,
    difficultyIndex: Int,
    val musicEntry: MaimaiMusicEntry
) {
    var difficultyName: String = ""

    var color: Color = maimaiDifficultyColors[difficultyIndex]
    var constant: String = ""
    var charter: String = ""
    var bestScore: String = "暂未游玩"
    var bestEntry: MaimaiBestScoreEntry? = null

    init {
        difficultyName = maimaiDifficultyTitles[difficultyIndex]
        constant = String.format("%.1f", musicEntry.constants[difficultyIndex])
        charter = musicEntry.charts[difficultyIndex].charter
        bestEntry = CFQUser.maimai.best.firstOrNull {
            it.title == title && it.levelIndex == difficultyIndex
        }
        if (bestEntry != null) {
            bestScore = String.format("%.4f", bestEntry!!.achievements) + "%"
        }
    }
}