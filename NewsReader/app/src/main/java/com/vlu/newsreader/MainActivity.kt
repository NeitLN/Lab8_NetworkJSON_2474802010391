package com.vlu.newsreader

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.vlu.newsreader.network.ApiConfig
import com.vlu.newsreader.network.HttpClient
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Temporary activity to check Step 3 (HTTP GET) in Logcat, Step 5 replaces it
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    HttpClient().get(ApiConfig.NEWS_URL)
                }
                Log.d("NewsReader", "status=${result.statusCode} length=${result.body.length}")
                Log.d("NewsReader", result.body.take(200))
            } catch (e: IOException) {
                Log.e("NewsReader", "Request failed", e)
            }
        }
    }
}
