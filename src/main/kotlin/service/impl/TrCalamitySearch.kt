package org.example.service.impl

import com.alibaba.fastjson.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Headers
import okhttp3.Request
import org.example.service.WikiSearchCommand
import java.io.IOException
import kotlin.random.Random

object TrCalamitySearch: WikiSearchCommand() {

    override val apiUrl = "https://calamity.huijiwiki.com/api.php"

    override val pageUrl = "https://calamity.huijiwiki.com/index.php?curid="

    override val command = "trc"

    override val name = "泰拉灾厄Wiki "

    override val desc = null

    private val userAgents = listOf(
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.2.1 Safari/605.1.15",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36 Edg/120.0.0.0"
    )

    private fun getRandomUserAgent(): String {
        return userAgents[Random.nextInt(userAgents.size)]
    }

    override suspend fun search(word: String): JSONObject {
        val params = mapOf(
            "action" to "query",
            "list" to "search",
            "srsearch" to word,
            "format" to "json",
            "srwhat" to "text"
        )

        // 构建完整的URL，包含查询参数
        val urlBuilder = StringBuilder(apiUrl).append("?")
        params.forEach { (key, value) ->
            urlBuilder.append("$key=$value&")
        }
        val url = urlBuilder.toString().dropLast(1)

        val headers = Headers.Builder()
            .add("User-Agent", getRandomUserAgent())
            .add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
            .add("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
//            .add("Accept-Encoding", "gzip, deflate, br")
            .add("Connection", "keep-alive")
            .add("Upgrade-Insecure-Requests", "1")
            .add("Sec-Fetch-Dest", "document")
            .add("Sec-Fetch-Mode", "navigate")
            .add("Sec-Fetch-Site", "none")
            .add("Sec-Fetch-User", "?1")
            .add("Cache-Control", "max-age=0")
            // 随机生成的 Cookie 示例，通常用于绕过某些反爬虫措施
            .add("Cookie", "cf_clearance=${generateRandomString(32)}; __cf_bm=${generateRandomString(64)}")
            .build()


        val request = Request.Builder()
            .url(url)
            .headers(headers)
            .get()
            .build()


        val response = withContext(Dispatchers.IO) {
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
        return JSONObject.parseObject(response)
    }

    private fun generateRandomString(length: Int): String {
        val allowedChars = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

//    override suspend fun search(word: String): JSONObject {
//        val params = mapOf(
//            "action" to "query",
//            "list" to "search",
//            "srsearch" to word,
//            "srwhat" to "text",
//            "format" to "json"
//        )
//        val result = getRequest(apiUrl, params)
//        return JSONObject.parseObject(result)
//    }
}