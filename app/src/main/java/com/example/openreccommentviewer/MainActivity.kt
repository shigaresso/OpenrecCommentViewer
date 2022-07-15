package com.example.openreccommentviewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.openreccommentviewer.ui.theme.OpenrecCommentViewerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OpenrecCommentViewerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    GetCommentButton()
                }
            }
        }
    }
}

@Composable
fun GetCommentButton() {
    // Compose でコルーチンを使うため
    val scope = rememberCoroutineScope()
    var isConnecting by remember { mutableStateOf(false) }
    Column {
        Button(
            enabled = !isConnecting,
            onClick = {
                isConnecting = true
                scope.launch(Dispatchers.IO) {
                    val httpClient = HttpClient()
                    val liveHtmlBody = httpClient.getHtmlBody(
                        urlString = "https://www.openrec.tv/live/1o8qy6ld1zk"
                    )
                    val userId = withContext(Dispatchers.Default) {
                        return@withContext extractString(
                            targetValue = liveHtmlBody,
                            extractPattern = """"channel":\{"user":\{"id":"(.+?)""""
                        )
                    }
                    val apiHtmlBody = httpClient.getHtmlBody(
                        urlString = "https://public.openrec.tv/external/api/v5/movies?channel_ids=$userId&sort=onair_status&is_upload=false"
                    )
                    val movieId = extractString(
                            targetValue = apiHtmlBody,
                            extractPattern = """movie_id":([0-9]+)"""
                        )
                    val client = WebSocketClient(movieId)
                    while (isConnecting) {
                        client.send()
                        withContext(Dispatchers.Default) {
                            println("25秒休みます: ${LocalDateTime.now()}")
                            delay(25000)
                        }
                    }
                }
            },
        ) {
            Text(text = "Get Comment")
        }

        Button(
            enabled = isConnecting,
            onClick = { isConnecting = false },
        ) {
            Text(text = "Disconnect")
        }

    }
}

suspend fun sleep() {
    withContext(Dispatchers.Default) {

    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    OpenrecCommentViewerTheme {
        GetCommentButton()
    }
}