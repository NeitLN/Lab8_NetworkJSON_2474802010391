package com.vlu.newsreader

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.vlu.newsreader.databinding.ActivityMainBinding
import com.vlu.newsreader.model.NewsItem
import com.vlu.newsreader.network.ApiConfig
import com.vlu.newsreader.network.HttpClient
import com.vlu.newsreader.parser.NewsJsonParser
import com.vlu.newsreader.ui.NewsAdapter
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val adapter = NewsAdapter()
    private val httpClient = HttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerNews.layoutManager = LinearLayoutManager(this)
        binding.recyclerNews.adapter = adapter
        binding.btnReload.setOnClickListener { loadNews() }
        binding.btnPost.setOnClickListener { sendTestPost() }

        loadNews()
    }

    // Thread map: launch (Main) -> HTTP on IO -> parsing on Default -> show result on Main
    private fun loadNews() {
        showLoading()
        lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    httpClient.get(ApiConfig.NEWS_URL)
                }
                if (!result.isSuccess) {
                    showStatus(getString(R.string.error_http, result.statusCode))
                    return@launch
                }
                val items = withContext(Dispatchers.Default) {
                    NewsJsonParser.parseManual(result.body)
                }
                Log.d(TAG, "loaded ${items.size} items")
                showContent(items)
            } catch (e: IOException) {
                // no internet, airplane mode or timeout all end up here
                Log.e(TAG, "Network failure", e)
                showStatus(getString(R.string.error_network, e.message.orEmpty()))
            } catch (e: JSONException) {
                Log.e(TAG, "JSON failure", e)
                showStatus(getString(R.string.error_parse, e.message.orEmpty()))
            }
        }
    }

    private fun sendTestPost() {
        val payload = JSONObject()
            .put("title", "Lab 8 test post")
            .put("body", "Created from NewsReader")
            .put("userId", 1)
            .toString()

        binding.btnPost.isEnabled = false
        lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    httpClient.post(ApiConfig.NEWS_URL, payload)
                }
                Log.d(TAG, "POST status=${result.statusCode} body=${result.body}")
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.post_result, result.statusCode),
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: IOException) {
                Log.e(TAG, "POST failure", e)
                // just a toast, a failed POST should not wipe the list that is already loaded
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.error_network, e.message.orEmpty()),
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                binding.btnPost.isEnabled = true
            }
        }
    }

    private fun showLoading() {
        binding.btnReload.isEnabled = false // so the same request is not sent twice
        binding.progressBar.visibility = View.VISIBLE
        binding.tvStatus.visibility = View.GONE
    }

    private fun showContent(items: List<NewsItem>) {
        binding.btnReload.isEnabled = true
        binding.progressBar.visibility = View.GONE
        if (items.isEmpty()) {
            showStatus(getString(R.string.empty_list))
            return
        }
        binding.tvStatus.visibility = View.GONE
        adapter.submitList(items)
    }

    private fun showStatus(message: String) {
        binding.btnReload.isEnabled = true
        binding.progressBar.visibility = View.GONE
        binding.tvStatus.text = message
        binding.tvStatus.visibility = View.VISIBLE
        adapter.submitList(emptyList())
    }

    companion object {
        private const val TAG = "NewsReader"
    }
}
