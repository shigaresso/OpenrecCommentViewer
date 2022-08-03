package com.example.openreccommentviewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime

class MainViewModel : ViewModel() {
    private val httpClient = HttpClient()
    private var webSocketClient = WebSocketClient()

    val isConnecting: StateFlow<Boolean> = webSocketClient.isConnecting

    var commentData = webSocketClient.commentData

    // 25 秒後にコメントサーバーに接続状態だと知らせるコルーチン用変数
    private var _connectJob: Job? = null

    fun connectLiveStream() {
        viewModelScope.launch {
            val liveHtmlBody = httpClient.getHtmlBody(
                urlString = "https://www.openrec.tv/live/pnzg6w6wvry"
            )
            val userId = extractString(
                targetValue = liveHtmlBody,
                extractPattern = """"channel":\{"user":\{"id":"(.+?)""""
            )
            val apiHtmlBody = httpClient.getHtmlBody(
                urlString = "https://public.openrec.tv/external/api/v5/movies?channel_ids=$userId&sort=onair_status&is_upload=false"
            )
            val movieId = extractString(
                targetValue = apiHtmlBody,
                extractPattern = """movie_id":([0-9]+)"""
            )
            webSocketClient.connectWebsocketServer(movieId)
            commentData = webSocketClient.commentData
            while (webSocketClient.isConnecting.value) {
                webSocketClient.send()
                withContext(Dispatchers.Default) {
                    println("25秒休みます: ${LocalDateTime.now()}")
                    _connectJob = launch {
                        delay(25000)
                    }
                }
            }
        }
    }

    fun disconnectLiveStream() {
        _connectJob?.cancel()
        _connectJob = null
        webSocketClient.disConnect()
    }
}