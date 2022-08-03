package com.example.openreccommentviewer.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.openreccommentviewer.Data

@Composable
fun CommentItem(data: Data?) {
    val imageUrl = data?.user_icon
    val painter = rememberAsyncImagePainter(model = imageUrl)
    Row(modifier = Modifier.padding(8.dp)) {
        Image(painter = painter, contentDescription = "コメントユーザーの画像", modifier = Modifier.size(50.dp))

        Column(modifier = Modifier.padding(horizontal = 6.dp)) {
            Text(text = data?.message_dt.toString())
            Text(text = data?.message.toString())
        }
    }
}