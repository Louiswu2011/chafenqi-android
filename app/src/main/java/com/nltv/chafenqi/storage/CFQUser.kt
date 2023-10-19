package com.nltv.chafenqi.storage

import android.util.Log
import com.nltv.chafenqi.networking.CFQServer

object CFQUser {
    private const val tag = "CFQUser"

    var token = ""

    var username = ""
    var isPremium = false

    suspend fun createProfile(authToken: String, username: String) {
        this.token = authToken
        this.username = username

        this.isPremium = CFQServer.apiIsPremium(username)

        Log.i(tag, "User is ${if (isPremium) "" else "not"} premium")
    }

    suspend fun loadProfileFromCache(targetUsername: String) {}
}