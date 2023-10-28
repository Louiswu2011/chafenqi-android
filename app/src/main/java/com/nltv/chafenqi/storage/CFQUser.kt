package com.nltv.chafenqi.storage

import android.util.Log
import com.nltv.chafenqi.networking.CFQServer
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiUserInfo
import com.onesignal.OneSignal

object CFQUser {
    private const val tag = "CFQUser"

    var token = ""

    var username = ""
    var isPremium = false

    var maimaiUserInfo = MaimaiUserInfo()

    suspend fun createProfile(authToken: String, username: String) {
        this.token = authToken
        this.username = username

        this.isPremium = CFQServer.apiIsPremium(username)

        Log.i(tag, "User is ${if (isPremium) "" else "not"} premium")
        registerOneSignal(username)
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