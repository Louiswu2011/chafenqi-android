package com.nltv.chafenqi.util

import androidx.navigation.NavController
import com.nltv.chafenqi.model.user.chunithm.UserChunithmRecentScoreEntry
import com.nltv.chafenqi.model.user.maimai.UserMaimaiRecentScoreEntry
import com.nltv.chafenqi.ChafenqiApplication
import com.nltv.chafenqi.storage.songlist.chunithm.ChunithmMusicEntry
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiMusicEntry
import com.nltv.chafenqi.storage.user.CFQUser
import com.nltv.chafenqi.view.home.HomeNavItem

fun navigateToMusicEntry(associatedMusicEntry: MaimaiMusicEntry, navController: NavController) {
    if (ChafenqiApplication.getRepository().maimaiMusicList.value.isEmpty()) return

    val maiMusicEntryIndex =
        ChafenqiApplication.getRepository().maimaiMusicList.value.indexOf(associatedMusicEntry)
    if (maiMusicEntryIndex < 0) return

    navController.navigate(HomeNavItem.SongList.route + "/maimai/$maiMusicEntryIndex")
}

fun navigateToMusicEntry(associatedMusicEntry: ChunithmMusicEntry, navController: NavController) {
    if (ChafenqiApplication.getRepository().chunithmMusicList.value.isEmpty()) return

    val chuMusicEntryIndex =
        ChafenqiApplication.getRepository().chunithmMusicList.value.indexOf(associatedMusicEntry)
    if (chuMusicEntryIndex < 0) return

    navController.navigate(HomeNavItem.SongList.route + "/chunithm/$chuMusicEntryIndex")
}

fun navigateToRecentEntry(recentEntry: UserMaimaiRecentScoreEntry, navController: NavController) {
    if (ChafenqiApplication.getRepository().maimaiMusicList.value.isEmpty()) return
    if (CFQUser.maimai.recent.isEmpty()) return

    val maiRecentEntryIndex =
        CFQUser.maimai.recent.indexOf(recentEntry)
    if (maiRecentEntryIndex < 0) return

    navController.navigate(HomeNavItem.Home.route + "/recent/maimai/$maiRecentEntryIndex")
}

fun navigateToRecentEntry(recentEntry: UserChunithmRecentScoreEntry, navController: NavController) {
    if (ChafenqiApplication.getRepository().chunithmMusicList.value.isEmpty()) return
    if (CFQUser.chunithm.recent.isEmpty()) return

    val chuRecentEntryIndex =
        CFQUser.chunithm.recent.indexOf(recentEntry)
    if (chuRecentEntryIndex < 0) return

    navController.navigate(HomeNavItem.Home.route + "/recent/chunithm/$chuRecentEntryIndex")
}