package com.nltv.chafenqi.networking

import android.util.Log
import io.ktor.client.request.accept
import io.ktor.client.request.cookie
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType

class FishServer {
    companion object {
        suspend fun fetchMaimaiMusicListData(): String {
            Log.i("FishServer", "Fetching maimai music list...")
            val response =
                CFQServer.client.get("https://www.diving-fish.com/api/maimaidxprober/music_data")
            return response.bodyAsText()
        }

        suspend fun checkTokenValidity(fishToken: String): Boolean {
            val response =
                CFQServer.client.get {
                    url("https://www.diving-fish.com/api/maimaidxprober/player/profile")
                    cookie("jwt_token", fishToken)
                    accept(ContentType.Application.Json)
                }
            return response.status.value == 200
        }
    }
}