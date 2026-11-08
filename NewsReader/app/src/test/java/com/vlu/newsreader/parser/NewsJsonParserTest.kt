package com.vlu.newsreader.parser

import com.vlu.newsreader.model.NewsItem
import org.json.JSONException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NewsJsonParserTest {

    // same shape as the real response, 2 posts are enough
    private val sample = """
        [
          {"userId": 1, "id": 1, "title": "first title", "body": "line 1\nline 2"},
          {"userId": 2, "id": 2, "title": "second title", "body": "hello"}
        ]
    """.trimIndent()

    @Test
    fun manualParser_readsAllFields() {
        val items = NewsJsonParser.parseManual(sample)

        assertEquals(2, items.size)
        assertEquals(NewsItem(id = 1, userId = 1, title = "first title", body = "line 1\nline 2"), items[0])
        assertEquals(2, items[1].userId)
    }

    @Test
    fun manualAndGson_giveTheSameList() {
        assertEquals(NewsJsonParser.parseManual(sample), NewsJsonParser.parseWithGson(sample))
    }

    @Test
    fun manualParser_usesDefaultsForOptionalKeys() {
        val item = NewsJsonParser.parseManual("""[{"id": 5}]""").first()

        assertEquals(NewsItem(id = 5, userId = 0, title = "", body = ""), item)
    }

    @Test(expected = JSONException::class)
    fun manualParser_throwsWhenIdIsMissing() {
        NewsJsonParser.parseManual("""[{"userId": 1, "title": "no id"}]""")
    }

    @Test(expected = JSONException::class)
    fun manualParser_throwsWhenRootIsAnObject() {
        // JSONArray(...) cannot read a "{" root
        NewsJsonParser.parseManual("""{"id": 1}""")
    }

    @Test
    fun emptyArray_givesEmptyList() {
        assertTrue(NewsJsonParser.parseManual("[]").isEmpty())
        assertTrue(NewsJsonParser.parseWithGson("[]").isEmpty())
    }
}
