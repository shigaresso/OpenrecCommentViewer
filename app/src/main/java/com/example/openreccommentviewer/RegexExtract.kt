package com.example.openreccommentviewer

fun extractString(targetValue: String, extractPattern: String): String {
    val regex = extractPattern.toRegex()
    val match = regex.find(targetValue)
    return match!!.groups[1]!!.value
}