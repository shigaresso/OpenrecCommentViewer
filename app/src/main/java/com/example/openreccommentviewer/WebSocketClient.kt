package com.example.openreccommentviewer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener

// OkHttp3 を用いて WebSocket 接続を使うには継承が必要
class WebSocketClient(movieId: String) : WebSocketListener() {
    private val ws: WebSocket

    init {
        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
            .url("wss://chat.openrec.tv/socket.io/?movieId=$movieId&EIO=3&transport=websocket")
            .build()
        val webSocket = okHttpClient.newWebSocket(request, this)
        ws = webSocket
    }

    fun disConnect() {
        ws.close(1000, "")
    }

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

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        println("WebSocket 接続を終了しました")
    }
}