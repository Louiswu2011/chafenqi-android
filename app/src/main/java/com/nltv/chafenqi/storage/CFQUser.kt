package com.nltv.chafenqi.storage

import android.util.Log
import com.nltv.chafenqi.networking.CFQServer
import com.nltv.chafenqi.view.LoginPageViewModel

object CFQUser {
    private val logTag = "CFQUser"

    private var token = ""

    var username = ""
    var isPremium = false

    suspend fun createProfile(authToken: String, username: String) {
        this.token = authToken
        this.username = username

        this.isPremium = CFQServer.apiIsPremium(username)

        Log.i(logTag, "User is ${if (isPremium) "" else "not"} premium")
    }
}