package com.vlu.newsreader.parser

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.vlu.newsreader.model.NewsItem
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Two ways to turn the same JSON into List<NewsItem>:
 * - parseManual: JSONArray / JSONObject, reads every key by hand
 * - parseWithGson: Gson maps the keys using @SerializedName
 */
object NewsJsonParser {

    private val gson = Gson()

    // getInt("id") throws JSONException if id is missing (id is required),
    // optInt / optString give a default value for the optional fields
    @Throws(JSONException::class)
    fun parseItem(obj: JSONObject): NewsItem = NewsItem(
        id = obj.getInt("id"),
        userId = obj.optInt("userId", 0),
        title = obj.optString("title", ""),
        body = obj.optString("body", "")
    )

    // root of the response is "[" so it is a JSONArray, every element is a "{" object
    @Throws(JSONException::class)
    fun parseManual(json: String): List<NewsItem> {
        val array = JSONArray(json)
        val items = ArrayList<NewsItem>(array.length())
        for (index in 0 until array.length()) {
            items.add(parseItem(array.getJSONObject(index)))
        }
        return items
    }

    @Throws(JsonParseException::class)
    fun parseWithGson(json: String): List<NewsItem> =
        gson.fromJson(json, Array<NewsItem>::class.java)?.toList() ?: emptyList()
}
