package com.nltv.chafenqi.view.home.recent

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.nltv.chafenqi.extension.toDateString
import com.nltv.chafenqi.extension.toMaimaiCoverPath
import com.nltv.chafenqi.extension.toRateString
import com.nltv.chafenqi.model.user.chunithm.UserChunithmRecentScoreEntry
import com.nltv.chafenqi.model.user.maimai.UserMaimaiRecentScoreEntry
import com.nltv.chafenqi.networking.CFQServer
import com.nltv.chafenqi.storage.persistent.CFQPersistentData
import com.nltv.chafenqi.storage.songlist.chunithm.ChunithmMusicEntry
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiMusicEntry
import com.nltv.chafenqi.storage.user.CFQUser
import com.nltv.chafenqi.view.home.HomeNavItem

class RecentDetailPageViewModel : ViewModel() {
    private val tag = this::class.java.canonicalName

    var mode: Int = 0

    var maiEntry: UserMaimaiRecentScoreEntry? = null
    var chuEntry: UserChunithmRecentScoreEntry? = null

    var maiMusic: MaimaiMusicEntry? = null
    var chuMusic: ChunithmMusicEntry? = null

    var coverUrl: String = ""
    var title: String = ""
    var artist: String = ""
    var playDateString: String = ""

    var score: String = ""
    var rateString: String = ""

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
    private var maiMusicEntryIndex = 0

    private var chuMusicEntryIndex = 0

    var canNavigate = false

    fun update(mode: Int, index: Int, context: Context) {
        this.mode = mode
        if (mode == 0 && CFQPersistentData.Chunithm.musicList.isNotEmpty() && CFQUser.chunithm.recent.isNotEmpty()) {
            chuEntry = CFQUser.chunithm.recent[index]
            chuMusic = chuEntry?.associatedMusicEntry

            coverUrl = "${CFQServer.defaultPath}/api/resource/chunithm/cover?musicId=${chuMusic?.musicId}"
            title = chuMusic?.title ?: ""
            artist = chuMusic?.artist ?: ""
            playDateString = chuEntry?.timestamp?.toDateString(context) ?: ""
            score = chuEntry?.score.toString()
            rateString = chuEntry?.score?.toRateString() ?: ""

            chuMusicEntryIndex =
                CFQPersistentData.Chunithm.musicList.indexOf(chuMusic ?: ChunithmMusicEntry())
            canNavigate = chuMusicEntryIndex != -1
        } else if (mode == 1 && CFQPersistentData.Maimai.musicList.isNotEmpty() && CFQUser.maimai.recent.isNotEmpty()) {
            maiEntry = CFQUser.maimai.recent[index]
            maiMusic =
                CFQPersistentData.Maimai.musicList.firstOrNull { it.title == maiEntry?.associatedMusicEntry?.title && it.type == maiEntry?.type }

            coverUrl = maiMusic?.coverId?.toMaimaiCoverPath() ?: ""
            title = maiMusic?.title ?: ""
            artist = maiMusic?.basicInfo?.artist ?: ""
            playDateString = maiEntry?.timestamp?.toDateString(context) ?: ""
            score = String.format("%.4f", maiEntry?.achievements) + "%"
            rateString = maiEntry?.achievements?.toRateString() ?: ""

            maiTap = maiEntry?.noteTap ?: listOf()
            maiHold = maiEntry?.noteHold ?: listOf()
            maiSlide = maiEntry?.noteSlide ?: listOf()
            maiTouch = maiEntry?.noteTouch ?: listOf()
            maiBreak = maiEntry?.noteBreak ?: listOf()
            maiJudges = listOf(maiTap, maiHold, maiSlide, maiTouch, maiBreak)

            maiCombo = maiEntry?.maxCombo ?: ""
            maiMatchingPlayers = listOf(
                maiEntry?.players?.getOrNull(0) ?: "-",
                maiEntry?.players?.getOrNull(1) ?: "-",
                maiEntry?.players?.getOrNull(2) ?: "-"
            )
            maiSync = maiEntry?.maxSync ?: ""
            maiHasSync = maiSync != "―"

            maiMusicEntryIndex =
                CFQPersistentData.Maimai.musicList.indexOf(maiMusic ?: MaimaiMusicEntry())
            canNavigate = maiMusicEntryIndex != -1
        }
    }

    fun navigateToMusicEntry(navController: NavController) {
        if (!canNavigate) return

        val navigateKeyword = if (mode == 0) "chunithm" else "maimai"
        val navigateIndex = if (mode == 0) chuMusicEntryIndex else maiMusicEntryIndex
        navController.navigate(HomeNavItem.SongList.route + "/$navigateKeyword/$navigateIndex")
    }
}