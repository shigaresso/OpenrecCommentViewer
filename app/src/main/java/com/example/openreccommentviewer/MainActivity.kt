package com.example.openreccommentviewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.openreccommentviewer.component.CommentItem
import com.example.openreccommentviewer.ui.theme.OpenrecCommentViewerTheme

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
fun GetCommentButton(viewModel: MainViewModel = MainViewModel()) {

    val isConnecting by viewModel.isConnecting.collectAsState()
    val commentData by viewModel.commentData.collectAsState()

    Column {
        Button(
            enabled = !isConnecting,
            onClick = { viewModel.connectLiveStream() },
        ) {
            Text(text = "Get Comment")
        }

        Button(
            enabled = isConnecting,
            onClick = { viewModel.disconnectLiveStream() },
        ) {
            Text(text = "Disconnect")
        }

        if (commentData.isNotEmpty()) {
            LazyColumn {
                items(commentData) { comment ->
                    CommentItem(comment.data)
//                    Text(text = comment.data?.message.toString())
//                    Text(text = comment.data?.user_name.toString())
                }
            }
//            Text(text = commentData.last().data?.message.toString())
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    OpenrecCommentViewerTheme {
        GetCommentButton()
    }
}