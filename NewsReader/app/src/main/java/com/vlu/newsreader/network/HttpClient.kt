package com.vlu.newsreader.network

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

data class HttpResult(val statusCode: Int, val body: String) {
    val isSuccess: Boolean
        get() = statusCode in 200..299
}

/**
 * Small blocking HTTP helper built on HttpURLConnection.
 * It never touches the UI and never picks a thread by itself,
 * the caller has to run it inside withContext(Dispatchers.IO).
 */
class HttpClient(
    private val connectTimeoutMs: Int = 10_000,
    private val readTimeoutMs: Int = 10_000
) {

    @Throws(IOException::class)
    fun get(urlString: String): HttpResult = execute("GET", urlString, null)

    @Throws(IOException::class)
    fun post(urlString: String, jsonBody: String): HttpResult =
        execute("POST", urlString, jsonBody)

    @Throws(IOException::class)
    fun put(urlString: String, jsonBody: String): HttpResult =
        execute("PUT", urlString, jsonBody)

    @Throws(IOException::class)
    fun delete(urlString: String): HttpResult = execute("DELETE", urlString, null)

    @Throws(IOException::class)
    private fun execute(method: String, urlString: String, jsonBody: String?): HttpResult {
        val connection = URL(urlString).openConnection() as HttpURLConnection
        try {
            connection.requestMethod = method
            connection.connectTimeout = connectTimeoutMs
            connection.readTimeout = readTimeoutMs
            connection.setRequestProperty("Accept", "application/json")

            if (jsonBody != null) {
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                connection.outputStream.use { output ->
                    output.write(jsonBody.toByteArray(Charsets.UTF_8))
                }
            }

            val statusCode = connection.responseCode
            // 4xx/5xx responses have their body in errorStream, not inputStream
            val stream = if (statusCode in 200..299) connection.inputStream else connection.errorStream
            val body = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() } ?: ""
            return HttpResult(statusCode, body)
        } finally {
            connection.disconnect()
        }
    }
}
