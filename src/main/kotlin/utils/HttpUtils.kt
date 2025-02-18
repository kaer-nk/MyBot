package org.example.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import java.io.IOException

private val client = OkHttpClient()

suspend fun getRequest(url: String, params: Map<String, String>): String? {
    val queryString = params.entries.joinToString("&") { "${it.key}=${it.value}" }
    val requestUrl = "$url?$queryString"
    val request = okhttp3.Request.Builder()
        .url(requestUrl)
        .get()
        .build()

    return withContext(Dispatchers.IO) {
        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    response.body?.string()
                } else {
                    "请求失败: ${response.code}"
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            "网络错误: ${e.message}"
        }
    }
}