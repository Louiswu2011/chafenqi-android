package com.nltv.chafenqi.util

import androidx.navigation.NavController
import com.nltv.chafenqi.model.user.chunithm.UserChunithmRecentScoreEntry
import com.nltv.chafenqi.model.user.maimai.UserMaimaiRecentScoreEntry
import com.nltv.chafenqi.storage.persistent.CFQPersistentData
import com.nltv.chafenqi.storage.songlist.chunithm.ChunithmMusicEntry
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiMusicEntry
import com.nltv.chafenqi.storage.user.CFQUser
import com.nltv.chafenqi.view.home.HomeNavItem

fun navigateToMusicEntry(associatedMusicEntry: MaimaiMusicEntry, navController: NavController) {
    if (CFQPersistentData.Maimai.musicList.isEmpty()) return

    val maiMusicEntryIndex =
        CFQPersistentData.Maimai.musicList.indexOf(associatedMusicEntry)
    if (maiMusicEntryIndex < 0) return

    navController.navigate(HomeNavItem.SongList.route + "/maimai/$maiMusicEntryIndex")
}

fun navigateToMusicEntry(associatedMusicEntry: ChunithmMusicEntry, navController: NavController) {
    if (CFQPersistentData.Chunithm.musicList.isEmpty()) return

    val chuMusicEntryIndex =
        CFQPersistentData.Chunithm.musicList.indexOf(associatedMusicEntry)
    if (chuMusicEntryIndex < 0) return

    navController.navigate(HomeNavItem.SongList.route + "/chunithm/$chuMusicEntryIndex")
}

fun navigateToRecentEntry(recentEntry: UserMaimaiRecentScoreEntry, navController: NavController) {
    if (CFQPersistentData.Maimai.musicList.isEmpty()) return
    if (CFQUser.maimai.recent.isEmpty()) return

    val maiRecentEntryIndex =
        CFQUser.maimai.recent.indexOf(recentEntry)
    if (maiRecentEntryIndex < 0) return

    navController.navigate(HomeNavItem.Home.route + "/recent/maimai/$maiRecentEntryIndex")
}

fun navigateToRecentEntry(recentEntry: UserChunithmRecentScoreEntry, navController: NavController) {
    if (CFQPersistentData.Chunithm.musicList.isEmpty()) return
    if (CFQUser.chunithm.recent.isEmpty()) return

    val chuRecentEntryIndex =
        CFQUser.chunithm.recent.indexOf(recentEntry)
    if (chuRecentEntryIndex < 0) return

    navController.navigate(HomeNavItem.Home.route + "/recent/chunithm/$chuRecentEntryIndex")
}