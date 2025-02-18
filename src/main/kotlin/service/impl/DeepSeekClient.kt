package org.example.service.impl

import ConfigManager
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.example.service.AIDialogCommand


object DeepSeekClient: AIDialogCommand() {

    override val command: String = "ds"

    override val name: String = "deepseek"

    override val apiKey: String

    override val baseUrl: String

    init {
        val dsConfig = ConfigManager.CONFIG.getJSONObject("deepseek")
        baseUrl = dsConfig.getString("url")
        apiKey = dsConfig.getString("apiKey")
    }

    override val models: Map<String, String> = mapOf(
        "r1" to "deepseek-reasoner",
        "v3" to "deepseek-chat"
    )

    override var nowModel: String = "deepseek-chat"

    override suspend fun getAIRes(message: List<JSONObject>): JSONObject {
        val requestBody = JSONObject().apply {
            put("model", nowModel)
            put("messages", message)
        }.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(baseUrl)
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Unexpected code $response")
            }

            val responseJson = response.body?.string()
            return JSONArray.parseObject(responseJson) ?: throw Exception("空回复 $responseJson")
        }
    }
}