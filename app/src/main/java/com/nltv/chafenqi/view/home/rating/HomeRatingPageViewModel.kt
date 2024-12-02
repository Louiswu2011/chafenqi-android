package com.nltv.chafenqi.view.home.rating

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.nltv.chafenqi.model.user.chunithm.UserChunithmRatingListEntry
import com.nltv.chafenqi.model.user.maimai.UserMaimaiBestScoreEntry
import com.nltv.chafenqi.storage.persistent.CFQPersistentData
import com.nltv.chafenqi.storage.user.CFQUser
import com.nltv.chafenqi.view.home.HomeNavItem

class HomeRatingPageViewModel : ViewModel() {
    var user = CFQUser
    var mode = user.mode

    var maiPastList: List<UserMaimaiBestScoreEntry> = listOf()
    var maiNewList: List<UserMaimaiBestScoreEntry> = listOf()
    var maiRating: String = ""
    var maiPastRating: String = ""
    var maiNewRating: String = ""

    var chuBestList: List<UserChunithmRatingListEntry> = listOf()
    var chuRecentList: List<UserChunithmRatingListEntry> = listOf()
    var chuRating: String = ""
    var chuBestRating: String = ""
    var chuRecentRating: String = ""

    var canNavigate = false
    var showShareDialog by mutableStateOf(false)

    fun update() {
        when (user.mode) {
            1 -> {
                val maimai = user.maimai
                maiPastList = maimai.aux.pastBest
                maiNewList = maimai.aux.newBest
                maiRating = maimai.info.lastOrNull()?.rating.toString()
                maiPastRating = maimai.aux.pastRating.toString()
                maiNewRating = maimai.aux.newRating.toString()
            }

            0 -> {
                val chunithm = user.chunithm
                chuBestList = chunithm.aux.bestList
                chuRecentList = chunithm.aux.recentList
                chuRating = String.format("%.2f", chunithm.info.lastOrNull()?.rating)
                chuBestRating = String.format("%.2f", chunithm.aux.bestRating)
                chuRecentRating = String.format("%.2f", chunithm.aux.recentRating)
            }
        }
    }

    fun navigateToMusicEntry(bestEntry: UserMaimaiBestScoreEntry, navController: NavController) {
        if (CFQPersistentData.Maimai.musicList.isEmpty()) return

        val maiMusicEntryIndex =
            CFQPersistentData.Maimai.musicList.indexOf(bestEntry.associatedMusicEntry)
        if (maiMusicEntryIndex < 0) return

        navController.navigate(HomeNavItem.SongList.route + "/maimai/$maiMusicEntryIndex")
    }

    fun navigateToMusicEntry(ratingEntry: UserChunithmRatingListEntry, navController: NavController) {
        if (CFQPersistentData.Chunithm.musicList.isEmpty()) return

        val chuMusicEntryIndex =
            CFQPersistentData.Chunithm.musicList.indexOf(ratingEntry.associatedMusicEntry)
        if (chuMusicEntryIndex < 0) return

        navController.navigate(HomeNavItem.SongList.route + "/chunithm/$chuMusicEntryIndex")
    }
}