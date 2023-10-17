package com.nltv.chafenqi.networking

import android.util.Log
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText

class FishServer {
    companion object {
        suspend fun fetchMaimaiMusicListData(): String {
            Log.i("FishServer" ,"Fetching maimai music list...")
            val response = CFQServer.client.get("https://www.diving-fish.com/api/maimaidxprober/music_data")
            return response.bodyAsText()
        }
    }
}