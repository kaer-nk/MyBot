package org.example.service

import com.alibaba.fastjson.JSONObject
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.MiraiExperimentalApi

abstract class WikiSearchCommand : GroupCommand() {

    abstract val apiUrl: String

    abstract val pageUrl: String

    // 定义一个搜索方法，返回搜索结果
    abstract suspend fun search(word: String): JSONObject

    @OptIn(MiraiExperimentalApi::class)
    override suspend fun execute(event: GroupMessageEvent, word: String) {
        val group = event.group
        val res = search(word)
        val builder = ForwardMessageBuilder(group)
        val botId = group.bot.id
        val botName = group.bot.nick

        builder.add(
            ForwardMessage.Node(
            senderId = botId,
            time = (System.currentTimeMillis() / 1000).toInt(),
            senderName = botName,
            messageChain = buildMessageChain {
                add(PlainText("相关搜索结果"))
            }
        ))

        var equalNode : ForwardMessage.Node? = null
        res.getJSONObject("query")?.getJSONArray("search")?.forEachIndexed { idx,data ->
            data as JSONObject
            val title = data.getString("title")
            val pageId = data.getInteger("pageid")
            val node = ForwardMessage.Node(
                senderId = botId,
                time = (System.currentTimeMillis() / 1000).toInt(),
                senderName = botName,
                messageChain = buildMessageChain {
                    add(PlainText("$idx. $title\n${pageUrl}$pageId"))
                }
            )
            if (title == word) {
                equalNode = node
            }
            builder.add(node)
        }
        if (builder.size > 1) {
            if (equalNode != null) {
                val msgText = equalNode!!.messageChain.content.replace(Regex("\\d+\\.\\s*"),"")
                val msg = MessageChainBuilder()
                    .append(At(event.sender)) // 艾特某人
                    .append(PlainText(" "))   // 艾特后加一个空格，避免和正文连接
                    .append(PlainText(msgText))   // 添加正文消息
                    .build()
                group.sendMessage(msg)
            }
            group.sendMessage(builder.build())
        } else {
            group.sendMessage("没有找到 '$word' 相关的内容")
        }
    }

}