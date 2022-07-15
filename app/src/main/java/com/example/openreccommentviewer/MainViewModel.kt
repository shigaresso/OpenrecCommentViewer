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
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.LocalDateTime

class MainViewModel : ViewModel() {
    private val httpClient = HttpClient()

    private val _isConnecting = MutableStateFlow(false)
    val isConnecting: StateFlow<Boolean> = _isConnecting.asStateFlow()

    var commentData: StateFlow<String> = MutableStateFlow("まだコメントを受け取っていません")

    fun changeIsConnect() {
        _isConnecting.value = !_isConnecting.value
    }

    private fun startGetComment(movieId: String, listener: WebSocketClient): StateFlow<String> {
        val httpClient = OkHttpClient()
        val request = Request.Builder()
            .url("wss://chat.openrec.tv/socket.io/?movieId=$movieId&EIO=3&transport=websocket")
            .build()

        val webSocket = httpClient.newWebSocket(request, listener)
        listener.ws = webSocket
        return listener.commentData
    }

    fun connectLiveStream() {
        viewModelScope.launch {
            val liveHtmlBody = httpClient.getHtmlBody(
                urlString = "https://www.openrec.tv/live/em8xg5gmvr2"
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
            changeIsConnect()
            val client = WebSocketClient()
            withContext(Dispatchers.IO) {
                commentData = startGetComment(movieId, client)
            }
            while (_isConnecting.value) {
                client.send()
                withContext(Dispatchers.Default) {
                    println("25秒休みます: ${LocalDateTime.now()}")
                    delay(25000)
                }
            }
        }
    }
}