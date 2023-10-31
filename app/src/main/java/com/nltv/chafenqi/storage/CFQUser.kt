package com.nltv.chafenqi.storage

import android.util.Log
import com.nltv.chafenqi.networking.CFQServer
import com.nltv.chafenqi.storage.datastore.user.chunithm.ChunithmBestScoreEntry
import com.nltv.chafenqi.storage.datastore.user.chunithm.ChunithmDeltaEntry
import com.nltv.chafenqi.storage.datastore.user.chunithm.ChunithmExtraEntry
import com.nltv.chafenqi.storage.datastore.user.chunithm.ChunithmRatingEntry
import com.nltv.chafenqi.storage.datastore.user.chunithm.ChunithmRecentScoreEntry
import com.nltv.chafenqi.storage.datastore.user.chunithm.ChunithmUserInfo
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiAvatarEntry
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiBestScoreEntry
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiDeltaEntry
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiExtraInfo
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiRecentScoreEntry
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiUserInfo
import com.onesignal.OneSignal

object CFQUser {
    private const val tag = "CFQUser"

    var token = ""

    var username = ""
    var isPremium = false

    var maimai = Maimai

    object Maimai {
        var info = MaimaiUserInfo()
        var best = listOf<MaimaiBestScoreEntry>()
        var recent = listOf<MaimaiRecentScoreEntry>()
        var delta = listOf<MaimaiDeltaEntry>()
        var extra = MaimaiExtraInfo()
    }

    object Chunithm {
        var info = ChunithmUserInfo()
        var best = listOf<ChunithmBestScoreEntry>()
        var recent = listOf<ChunithmRecentScoreEntry>()
        var delta = listOf<ChunithmDeltaEntry>()
        var rating = listOf<ChunithmRatingEntry>()
        var extra = ChunithmExtraEntry()
    }

    suspend fun createProfile(authToken: String, username: String) {
        this.token = authToken
        this.username = username

        this.isPremium = CFQServer.apiIsPremium(username)

        Log.i(tag, "User is ${if (isPremium) "" else "not"} premium")
        // registerOneSignal(username)
    }

    suspend fun loadProfileFromCache(targetUsername: String) {}

    fun registerOneSignal(username: String) {
        OneSignal.login(username)
        Log.i(tag, "Registered OneSignal as external ID \"$username\".")
    }

    fun logoutOneSignal() {
        OneSignal.logout()
        Log.i(tag, "Logged out from OneSignal.")
    }
}