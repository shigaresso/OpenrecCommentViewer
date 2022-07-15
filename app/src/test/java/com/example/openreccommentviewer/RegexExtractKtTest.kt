package com.example.openreccommentviewer

import org.junit.Assert.*
import org.junit.Test

class RegexExtractKtTest {
    @Test
    fun extractUserId() {
        // act
        val actual = extractString(
            targetValue = "eCount\":-1}],\"channel\":{\"user\":{\"id\":\"sumomo_xqx\",\"userKey\"",
            extractPattern = """"channel":\{"user":\{"id":"(.+?)"""",
        )
        // assert
        assertEquals("sumomo_xqx", actual)
    }

    @Test
    fun extractMovieId() {
        val target = """
            [
	            {
		            "id": "o7z4e2x2q8l",
		            "movie_id": 2713650,
		            "title": "新作女の子部屋に憑き配信者に導く神ゲー「ノゾムキミノミライ」やるぞ！！",
                }
            ]
        """
        // act
        val actual = extractString(
            targetValue = target,
            extractPattern = """movie_id": ([0-9]+)""",
        )
        // assert
        assertEquals("2713650", actual)
    }
}