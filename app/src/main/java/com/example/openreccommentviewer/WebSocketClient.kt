package com.example.openreccommentviewer

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener

// OkHttp3 を用いて WebSocket 接続を使うには継承が必要
class WebSocketClient : WebSocketListener() {
    private val ws: WebSocket

    init {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("wss://chat.openrec.tv/socket.io/?movieId=2713447&uuid=655DF103-EB90-96BE-F7BB-305E1E8F094B&referrer=https%3A%2F%2Fwww.openrec.tv%2Flive%2Fnqr603w7gr6&connectAt=1657818667&connectionId=55e0772d-b661-40c2-8b42-f29d66e1dd8e&isExcludeLiveViewers=true&accessToken=dc6260a5-51f4-49f0-866a-81779a4c36bd&EIO=3&transport=websocket")
            .build()

        ws = client.newWebSocket(request, this)
    }

    fun send() {
        ws.send("2")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        println("Received text message: $text")
    }
}