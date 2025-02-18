package org.example.service

import com.alibaba.fastjson.JSONObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.MiraiExperimentalApi
import java.util.concurrent.ConcurrentHashMap

abstract class AIDialogCommand : GroupCommand() {

    abstract val apiKey: String

    abstract val baseUrl: String

    override val desc = null

    abstract val models: Map<String, String>

    abstract var nowModel: String

    private val msgMap = ConcurrentHashMap<String, JSONObject>()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                msgMap.forEach { (key, value) ->
                    val exp = value.getLong("exp")
                    if (System.currentTimeMillis() > exp) {
                        println("remove $key")
                        msgMap.remove(key)
                    }
                }
                delay(60000)
            }
        }
    }

    private suspend fun clearMsgEvent(event: GroupMessageEvent) {
        val key = "${event.group.id}-${event.sender.id}"
        msgMap.remove(key)
        val msg = MessageChainBuilder().append(At(event.sender)).append(" 消息上下文已清除").build()
        event.group.sendMessage(msg)
    }

    private suspend fun changeModel(event: GroupMessageEvent, content: String) {
        if (event.sender.id != 1950083680L) {
            event.group.sendMessage("权限不足")
            return
        }
        val modelCheck = models[content]
        if (modelCheck == null) {
            event.group.sendMessage("${name}模型不存在: $content")
            return
        }
        nowModel = modelCheck
        event.group.sendMessage("${name}模型已切换为: $nowModel")
    }

    // 定义一个搜索方法，返回搜索结果
    abstract suspend fun getAIRes(message: List<JSONObject>): JSONObject

    override suspend fun execute(event: GroupMessageEvent, word: String) {
        val args = word.split(" ")
        when (args[0]) {
            "-change" -> {
                try {
                    changeModel(event, args[1].lowercase())
                } catch (e: Exception) {
                    event.group.sendMessage("参数错误")
                }
            }
            "-c","-clear" -> {
                clearMsgEvent(event)
            }
            else -> {
                sendGroupMsg(event, word)
            }
        }
    }

    @OptIn(MiraiExperimentalApi::class)
    suspend fun sendGroupMsg(event: GroupMessageEvent, word: String) {
        val key = "${name}-${event.group.id}-${event.sender.id}"
        val msgHistory = msgMap[key] ?: JSONObject()
        val msgList = msgHistory.getJSONArray("messages")?.map { it as JSONObject }?.toMutableList() ?: mutableListOf(
            JSONObject().apply {
                put("role", "system")
                put("content", "只用纯文本回复,不要携带md格式")
            }
        )

        val thisNode = JSONObject().apply {
            put("role", "user")
            put("content", word)
        }
        msgList.add(thisNode)
        val (res,reasoningContent) = try {
            val respBody = getAIRes(msgList)
            val aiMsgBody = respBody.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
            aiMsgBody.getString("content") to aiMsgBody.getString("reasoning_content")
        } catch (e: Exception) {
            e.printStackTrace()
            val errorRefund = MessageChainBuilder()
                .append(QuoteReply(event.message))
                .append(At(event.sender))
                .append("\n$nowModel Api异常无法回复")
                .build()
            event.group.sendMessage(errorRefund)
            return
        }

        val assNode = JSONObject().apply {
            put("role", "assistant")
            put("content", res)
        }
        msgList.add(assNode)
        msgHistory["messages"] = msgList
        msgHistory["exp"] = System.currentTimeMillis() + 1000 * 60 * 5 // 5 分钟过期
        msgMap[key] = msgHistory

        val builder = ForwardMessageBuilder(event.group)
        builder.add(
            ForwardMessage.Node(
                senderId = event.bot.id,
                time = (System.currentTimeMillis() / 1000).toInt(),
                senderName = event.bot.nick,
                messageChain = buildMessageChain {
                    append(At(event.sender))
                }
            ))
        builder.add(
            ForwardMessage.Node(
                senderId = 1L,
                time = (System.currentTimeMillis() / 1000).toInt(),
                senderName = nowModel,
                messageChain = buildMessageChain {
                    add(PlainText(res))
                }
            ))
        if (!reasoningContent.isNullOrBlank()) {
            builder.add(
                ForwardMessage.Node(
                    senderId = 1L,
                    time = (System.currentTimeMillis() / 1000).toInt(),
                    senderName = nowModel,
                    messageChain = buildMessageChain {
                        add(PlainText("思维链: \n"))
                        add(PlainText(reasoningContent))
                    }
                ))
        }
        val msg = MessageChainBuilder().append(At(event.sender)).build()
        event.group.sendMessage(msg)
        event.group.sendMessage(builder.build())
    }

}