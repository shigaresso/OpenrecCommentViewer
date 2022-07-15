package com.example.openreccommentviewer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.IOException

class HttpClient(private val client: OkHttpClient = OkHttpClient()) {

    suspend fun getHtmlBody(): String {
        val request = Request.Builder()
            .url("https://www.openrec.tv/live/o7z4e2x2q8l")
            .build()

        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            return@withContext response.body?.string().orEmpty()
        }
    }
}