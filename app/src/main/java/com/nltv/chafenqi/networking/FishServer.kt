package com.nltv.chafenqi.networking

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json

class FishServer {
    companion object {
        private val client = HttpClient(OkHttp) {
            defaultRequest {
                url("https://www.diving-fish.com/api/")
            }
            install(ContentNegotiation) {
                json()
            }
            install(Logging) {
                level = LogLevel.ALL
                logger = Logger.DEFAULT
            }
        }

        suspend fun fetchMaimaiMusicListData(): String {
            Log.i("FishServer" ,"Fetching maimai music list...")
            val response = client.get("maimaidxprober/music_data")
            return response.bodyAsText()
        }
    }
}