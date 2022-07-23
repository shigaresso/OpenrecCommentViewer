package com.example.openreccommentviewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class MainViewModel : ViewModel() {
    private val httpClient = HttpClient()
    private var webSocketClient = WebSocketClient()

    val isConnecting: StateFlow<Boolean> = webSocketClient.isConnecting

    var commentData: StateFlow<Comment> = MutableStateFlow(Comment(null, "", 0))

    fun connectLiveStream() {
        viewModelScope.launch {
            val liveHtmlBody = httpClient.getHtmlBody(
                urlString = "https://www.openrec.tv/live/mlrl7ly3pzg"
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
                    delay(25000)
                }
            }
        }
    }

    fun disconnectLiveStream() {
        webSocketClient.disConnect()
    }
}