package org.example.service.impl

import com.alibaba.fastjson.JSONObject
import org.example.service.WikiSearchCommand
import org.example.utils.getRequest

object TrSearch: WikiSearchCommand() {

    override val apiUrl = "https://wiki.biligame.com/tr/api.php"

    override val pageUrl = "https://wiki.biligame.com/tr/index.php?curid="

    override val command = "tr"

    override val name = "泰拉Wiki "

    override val desc = null

    override suspend fun search(word: String): JSONObject {
        val params = mapOf(
            "action" to "query",
            "list" to "search",
            "srsearch" to word,
            "format" to "json"
        )
        val result = getRequest(apiUrl, params)
        return JSONObject.parseObject(result)
    }
}