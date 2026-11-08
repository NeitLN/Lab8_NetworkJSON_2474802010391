package com.vlu.buggyreader

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.vlu.buggyreader.databinding.ActivityMainBinding
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnLoad.setOnClickListener { loadPosts() }
    }

    // Fix #1: the request and the parsing now run on Dispatchers.IO,
    // only the TextView update is back on the Main thread
    private fun loadPosts() {
        binding.btnLoad.isEnabled = false
        lifecycleScope.launch {
            try {
                val titles = withContext(Dispatchers.IO) { downloadTitles() }
                binding.tvResult.text = titles
            } catch (e: IOException) {
                binding.tvResult.text = "Network error: ${e.message}"
            } finally {
                binding.btnLoad.isEnabled = true
            }
        }
    }

    // blocking code, must not be called on the Main thread
    private fun downloadTitles(): String {
        val connection = URL("https://jsonplaceholder.typicode.com/posts")
            .openConnection() as HttpURLConnection
        try {
            connection.requestMethod = "GET"
            val json = connection.inputStream.bufferedReader().use { it.readText() }

            val array = JSONArray(json)
            val builder = StringBuilder()
            for (index in 0 until array.length()) {
                builder.append(array.getJSONObject(index).getString("title"))
                builder.append(System.lineSeparator())
            }
            return builder.toString()
        } finally {
            connection.disconnect()
        }
    }
}
