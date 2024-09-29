package com.nltv.chafenqi.view.home.rating

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.nltv.chafenqi.networking.CFQServer
import com.nltv.chafenqi.storage.datastore.user.chunithm.ChunithmRatingEntry
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiBestScoreEntry
import com.nltv.chafenqi.storage.persistent.CFQPersistentData
import com.nltv.chafenqi.storage.user.CFQUser
import com.nltv.chafenqi.view.home.HomeNavItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File

class HomeRatingPageViewModel : ViewModel() {
    var user = CFQUser
    var mode = user.mode

    var maiPastList: List<MaimaiBestScoreEntry> = listOf()
    var maiNewList: List<MaimaiBestScoreEntry> = listOf()
    var maiRating: String = ""
    var maiPastRating: String = ""
    var maiNewRating: String = ""

    var chuBestList: List<ChunithmRatingEntry> = listOf()
    var chuRecentList: List<ChunithmRatingEntry> = listOf()
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
                maiRating = maimai.info.rating.toString()
                maiPastRating = maimai.aux.pastRating.toString()
                maiNewRating = maimai.aux.newRating.toString()
            }

            0 -> {
                val chunithm = user.chunithm
                chuBestList = chunithm.aux.bestList
                chuRecentList = chunithm.aux.recentList
                chuRating = String.format("%.2f", chunithm.info.rating)
                chuBestRating = String.format("%.2f", chunithm.aux.bestRating)
                chuRecentRating = String.format("%.2f", chunithm.aux.recentRating)
            }
        }
    }

    fun navigateToMusicEntry(bestEntry: MaimaiBestScoreEntry, navController: NavController) {
        if (CFQPersistentData.Maimai.musicList.isEmpty()) return

        val maiMusicEntryIndex =
            CFQPersistentData.Maimai.musicList.indexOf(bestEntry.associatedMusicEntry)
        if (maiMusicEntryIndex < 0) return

        navController.navigate(HomeNavItem.SongList.route + "/maimai/$maiMusicEntryIndex")
    }

    fun navigateToMusicEntry(ratingEntry: ChunithmRatingEntry, navController: NavController) {
        if (CFQPersistentData.Chunithm.musicList.isEmpty()) return

        val chuMusicEntryIndex =
            CFQPersistentData.Chunithm.musicList.indexOf(ratingEntry.associatedMusicEntry)
        if (chuMusicEntryIndex < 0) return

        navController.navigate(HomeNavItem.SongList.route + "/chunithm/$chuMusicEntryIndex")
    }
}