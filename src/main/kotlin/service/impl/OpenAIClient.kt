package org.example.service.impl

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.example.service.AIDialogCommand
import javax.swing.text.html.parser.DTDConstants.MODEL

object OpenAIClient: AIDialogCommand() {

    override val command: String = "gpt"

    override val name: String = "chatGpt"

    override val apiKey: String

    override val baseUrl: String

    override val models: Map<String, String> = mapOf(
        "4o-m" to "gpt-4o-mini",
        "4o" to "gpt-4o",
        "4o1-m" to "o1-mini",
        "4o-all" to "gpt-4o-all",
    )

    init {
        val openAIConfig = ConfigManager.CONFIG.getJSONObject("openAI")
        baseUrl = openAIConfig.getString("url")
        apiKey = openAIConfig.getString("apiKey")
    }

    override var nowModel: String = "gpt-4o-mini"

    override suspend fun getAIRes(message: List<JSONObject>): JSONObject {
        val requestBody = JSONObject().apply {
            put("model", nowModel)
            put("formatting","plain_text")
            put("messages", message)
        }.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(baseUrl)
//            .addHeader(HttpHeaders.Accept, "text/plain")
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
