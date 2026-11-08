package com.vlu.buggyreader

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vlu.buggyreader.databinding.ActivityMainBinding
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONArray

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnLoad.setOnClickListener { loadPosts() }
    }

    private fun loadPosts() {
        val connection = URL("https://jsonplaceholder.typicode.com/posts")
            .openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        val json = connection.inputStream.bufferedReader().use { it.readText() }

        val array = JSONArray(json)
        val builder = StringBuilder()
        for (index in 0 until array.length()) {
            builder.append(array.getJSONObject(index).getString("title"))
            builder.append(System.lineSeparator())
        }
        binding.tvResult.text = builder.toString()
    }
}
