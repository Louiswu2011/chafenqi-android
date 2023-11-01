package com.nltv.chafenqi.view.home.recent

import android.util.Log
import androidx.lifecycle.ViewModel
import com.nltv.chafenqi.extension.toDateString
import com.nltv.chafenqi.extension.toMaimaiCoverPath
import com.nltv.chafenqi.extension.toMaimaiCoverString
import com.nltv.chafenqi.storage.CFQUser
import com.nltv.chafenqi.storage.datastore.user.chunithm.ChunithmRecentScoreEntry
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiRecentScoreEntry
import com.nltv.chafenqi.storage.`object`.CFQPersistentData
import com.nltv.chafenqi.storage.songlist.chunithm.ChunithmMusicEntry
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiMusicEntry

class RecentDetailPageViewModel: ViewModel() {
    private val tag = this::class.java.canonicalName

    var maiEntry: MaimaiRecentScoreEntry? = null
    var chuEntry: ChunithmRecentScoreEntry? = null

    var maiMusic: MaimaiMusicEntry? = null
    var chuMusic: ChunithmMusicEntry? = null

    var coverUrl: String = ""
    var title: String = ""
    var artist: String = ""
    var playDateString: String = ""

    var score: String = ""

    val maiJudgeTexts = listOf("Critical", "Perfect", "Great", "Good", "Miss")
    var maiJudges: List<List<String>> = listOf()
    var maiTap: List<String> = listOf()
    var maiHold: List<String> = listOf()
    var maiSlide: List<String> = listOf()
    var maiTouch: List<String> = listOf()
    var maiBreak: List<String> = listOf()
    var maiCombo: String = ""

    var maiHasSync: Boolean = false
    var maiMatchingPlayers: List<String> = listOf()
    var maiSync: String = ""
    var maiMusicEntryIndex = 0

    fun update(mode: Int, index: Int) {
        if (mode == 0 && CFQPersistentData.Chunithm.musicList.isNotEmpty()) {
            chuEntry = CFQUser.chunithm.recent[index]
            chuMusic = CFQPersistentData.Chunithm.musicList.firstOrNull { it.musicID.toString() == chuEntry?.idx }
            Log.i(tag, "Loaded chunithm music ${chuMusic?.title} from ${chuEntry?.title}")

            coverUrl = "http://43.139.107.206:8083/api/chunithm/cover?musicId=${chuMusic?.musicID}"
            title = chuMusic?.title ?: ""
            artist = chuMusic?.artist ?: ""
            playDateString = chuEntry?.timestamp?.toDateString() ?: ""
            score = chuEntry?.score.toString()

        } else if (mode == 1 && CFQPersistentData.Maimai.musicList.isNotEmpty()) {
            maiEntry = CFQUser.maimai.recent[index]
            maiMusic = CFQPersistentData.Maimai.musicList.firstOrNull { it.title == maiEntry?.title }
            Log.i(tag, "Loaded maimai music ${maiMusic?.title} from ${maiEntry?.title}")

            coverUrl = maiMusic?.id?.toMaimaiCoverPath() ?: ""
            title = maiMusic?.title ?: ""
            artist = maiMusic?.basicInfo?.artist ?: ""
            playDateString = maiEntry?.timestamp?.toDateString() ?: ""
            score = String.format("%.4f", maiEntry?.achievements) + "%"

            maiTap = maiEntry?.notesTap?.split(",") ?: listOf()
            maiHold = maiEntry?.notesHold?.split(",") ?: listOf()
            maiSlide = maiEntry?.notesSlide?.split(",") ?: listOf()
            maiTouch = maiEntry?.notesTouch?.split(",") ?: listOf()
            maiBreak = maiEntry?.notesBreak?.split(",") ?: listOf()
            maiJudges = listOf(maiTap, maiHold, maiSlide, maiTouch, maiBreak)

            maiCombo = maiEntry?.maxCombo ?: ""
            maiMatchingPlayers = listOf(maiEntry?.matching1 ?: "-", maiEntry?.matching2 ?: "-", maiEntry?.matching3 ?: "-")
            maiSync = maiEntry?.maxSync ?: ""
            maiHasSync = maiSync != "―"

            maiMusicEntryIndex = CFQPersistentData.Maimai.musicList.indexOf(maiMusic ?: MaimaiMusicEntry())
        }
    }
}