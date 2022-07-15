package com.example.openreccommentviewer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import okhttp3.WebSocket
import okhttp3.WebSocketListener

// OkHttp3 を用いて WebSocket 接続を使うには継承が必要
class WebSocketClient : WebSocketListener() {
    lateinit var ws: WebSocket

    private val _commentData = MutableStateFlow("")
    val commentData: StateFlow<String> = _commentData

    private fun outputData(string: String) {
        _commentData.value = string
    }

    suspend fun send() {
        withContext(Dispatchers.IO) {
            ws.send("2")
        }
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        println("Received text message: $text")
        outputData(text)
    }
}