package com.vlu.newsreader

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.vlu.newsreader.network.ApiConfig
import com.vlu.newsreader.network.HttpClient
import com.vlu.newsreader.parser.NewsJsonParser
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException

// Temporary activity to check Step 4 (manual vs Gson parsing) in Logcat, Step 5 replaces it
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    HttpClient().get(ApiConfig.NEWS_URL)
                }
                val manual = NewsJsonParser.parseManual(result.body)
                val automated = NewsJsonParser.parseWithGson(result.body)
                Log.d("NewsReader", "manual=${manual.size} gson=${automated.size} equal=${manual == automated}")
                Log.d("NewsReader", manual.first().toString())
            } catch (e: IOException) {
                Log.e("NewsReader", "Request failed", e)
            } catch (e: JSONException) {
                Log.e("NewsReader", "Parsing failed", e)
            }
        }
    }
}
