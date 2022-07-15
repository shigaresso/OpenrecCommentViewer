package com.example.openreccommentviewer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener

// OkHttp3 を用いて WebSocket 接続を使うには継承が必要
class WebSocketClient(movieId: String) : WebSocketListener() {
    private val ws: WebSocket

    init {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("wss://chat.openrec.tv/socket.io/?movieId=$movieId&EIO=3&transport=websocket")
            .build()

        ws = client.newWebSocket(request, this)
    }

    suspend fun send() {
        withContext(Dispatchers.IO) {
            ws.send("2")
        }
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        println("Received text message: $text")
    }
}