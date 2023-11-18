package com.nltv.chafenqi.networking

import android.util.Log
import io.ktor.client.request.accept
import io.ktor.client.request.cookie
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentLength
import io.ktor.http.contentType

class FishServer {
    companion object {
        suspend fun fetchMaimaiMusicListData(): String {
            Log.i("FishServer", "Fetching maimai music list...")
            val response =
                CFQServer.client.get("https://www.diving-fish.com/api/maimaidxprober/music_data")
            return response.bodyAsText()
        }

        suspend fun getUserToken(username: String, password: String): String {
            try {
                val response =
                    CFQServer.client.post {
                        url("https://www.diving-fish.com/api/maimaidxprober/login")
                        contentType(ContentType.Application.Json)
                        accept(ContentType.Application.Json)
                        setBody(
                            hashMapOf(
                                "username" to username,
                                "password" to password
                            )
                        )
                        contentLength()
                    }

                if (response.status.value == 401) {
                    return ""
                }

                val cookies = response.headers["Set-Cookie"] ?: ""
                if (cookies.isEmpty()) {
                    Log.e("FishServer", "Set-Cookie is empty :(")
                    return ""
                }
                val tokenComponent = cookies.split(";")[0]
                val token = tokenComponent.substringAfter("=", "")
                if (token.isEmpty()) {
                    Log.e("FishServer", "Cannot parse token component $tokenComponent")
                    return ""
                }

                return token
            } catch (e: Exception) {
                Log.e("FishServer", "Cannot get fish token, error: $e")
                return ""
            }
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