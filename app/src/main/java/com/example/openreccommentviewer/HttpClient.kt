package com.example.openreccommentviewer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.IOException

class HttpClient {
    private val client = OkHttpClient()

    suspend fun getHtmlBody(): String {
        val request = Request.Builder()
            .url("https://www.openrec.tv/live/lv816kd7yz9")
            .build()

        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            return@withContext response.body?.string().orEmpty()
        }
    }

    fun getHtml() {
        val liveUrl = ""

    }
}