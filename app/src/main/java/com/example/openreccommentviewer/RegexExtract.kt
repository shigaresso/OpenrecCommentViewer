package com.example.openreccommentviewer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun extractString(targetValue: String, extractPattern: String): String {
    return withContext(Dispatchers.Default) {
        val regex = extractPattern.toRegex()
        val match = regex.find(targetValue)
        return@withContext match!!.groups[1]!!.value
    }
}