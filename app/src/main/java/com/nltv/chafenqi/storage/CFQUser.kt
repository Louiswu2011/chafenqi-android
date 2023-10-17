package com.nltv.chafenqi.storage

import android.util.Log
import com.nltv.chafenqi.networking.CFQServer
import com.nltv.chafenqi.view.LoginPageViewModel

class CFQUser {
    private val logTag = "CFQUser"

    private var token = ""

    var username = ""
    var isPremium = false

    suspend fun createProfile(authToken: String, username: String, model: LoginPageViewModel) {
        this.token = authToken
        this.username = username

        model.loginPromptText = "检查订阅状态..."
        this.isPremium = CFQServer.apiIsPremium(authToken)

        Log.i(logTag, "User is ${if (isPremium) "" else "not"} premium")
    }
}