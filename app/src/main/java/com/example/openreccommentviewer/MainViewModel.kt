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

    private val _isConnect: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isConnect: StateFlow<Boolean> = _isConnect.asStateFlow()

    private fun changeIsConnect() {
        _isConnect.value = !_isConnect.value
    }

    suspend fun connectLiveStream() {
        viewModelScope.launch {
            val liveHtmlBody = httpClient.getHtmlBody(
                urlString = "https://www.openrec.tv/live/n9ze7g7o5r4"
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
            println("押す前：${_isConnect.value}")
            changeIsConnect()
            println("押した後：${_isConnect.value}")
            val client = WebSocketClient(movieId)
            while (_isConnect.value) {
                client.send()
                withContext(Dispatchers.Default) {
                    println("25秒休みます: ${LocalDateTime.now()}")
                    delay(25000)
                }
            }
        }
    }
}