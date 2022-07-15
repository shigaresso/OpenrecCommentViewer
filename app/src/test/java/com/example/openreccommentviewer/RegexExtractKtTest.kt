package com.example.openreccommentviewer

import org.junit.Assert.*
import org.junit.Test

class RegexExtractKtTest {
    @Test
    fun extractUserId() {
        // act
        val actual = extractString(targetValue = "eCount\":-1}],\"channel\":{\"user\":{\"id\":\"sumomo_xqx\",\"userKey\"", extractPattern = """"channel":\{"user":\{"id":"(.+?)"""")
        // assert
        assertEquals("sumomo_xqx", actual)
    }
}