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
                    val client = WebSocketClient()
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

        Button(
            onClick = {
                scope.launch(Dispatchers.Default) {
                    val client = HttpClient().getHtmlBody()
                    withContext(Dispatchers.Default) {
                        println(client)
                    }
                }
            },
        ) {
            Text(text = "Get HTML body")
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