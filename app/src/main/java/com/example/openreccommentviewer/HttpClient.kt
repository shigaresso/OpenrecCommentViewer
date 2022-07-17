package com.example.openreccommentviewer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*

class HttpClient(private val client: OkHttpClient = OkHttpClient()) {

    suspend fun getHtmlBody(urlString: String): String {
        val request = Request.Builder()
            .url(urlString)
            .build()

        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            return@withContext response.body?.string().orEmpty()
        }
    }
}