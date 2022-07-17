package com.example.openreccommentviewer

import com.google.gson.Gson
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

    private val _commentData = MutableStateFlow(Comment(null, "", 0))
    val commentData: StateFlow<Comment> = _commentData

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

    private fun outputData(text: String) {
        when (text.substring(0, 1)) {
            // 最初の 1 文字が 4 以外は視聴者のコメントではない
            "4" -> {
                val a = 1
                when (text.substring(0, 2)) {
                    // substring は JSON に不要な部分を削除している
                    "42" -> {
                        val comment = text.substring(14, text.length - 2).replace("\\", "")
                        println(comment)
                        val json = Gson().fromJson(comment, Comment::class.java)
                        println("JSONは $json")
                        _commentData.value = json
                    }
                }
            }
        }
    }

    suspend fun send() {
        withContext(Dispatchers.IO) {
            ws.send("2")
        }
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
//        println("Received text message: $text")
        outputData(text)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        println("WebSocket 接続を終了しました")
    }
}