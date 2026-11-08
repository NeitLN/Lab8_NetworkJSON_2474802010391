package com.vlu.newsreader.model

import com.google.gson.annotations.SerializedName

// One post from https://jsonplaceholder.typicode.com/posts
// @SerializedName keeps the JSON key names, the manual parser uses the same keys
data class NewsItem(
    @SerializedName("id") val id: Int,
    @SerializedName("userId") val userId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String
)
