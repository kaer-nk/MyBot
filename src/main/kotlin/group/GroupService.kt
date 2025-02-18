package org.example.group

import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.At

interface GroupService {

    val groupCommandRegistry: GroupCommandRegistry

    suspend fun invoke(event: GroupMessageEvent) {
        val botId = event.bot.id
        // 被 @ 的情况
        val content = event.message
            .filterNot { it is At }
            .joinToString("") { it.contentToString().trim() }
            .trim()
        //拆为2个参数  只要第一个参数
        val arg = content.split(" ")[0]
        val text = content.removePrefix(arg).trim()

        // 尝试从命令注册中心获取匹配的命令对象
        val commandHandler = groupCommandRegistry.getCommand(arg.lowercase())
        if (commandHandler != null) {
            // 找到对应命令时，调用对应的处理函数
            commandHandler.execute(event, text)
        } else {
            // 如果命令不存在，则检查是否 @ 了机器人
            val atMention = event.message.firstOrNull { it is At } as? At
            if (atMention != null && atMention.target == botId) {
                // 构建并发送支持的命令列表消息
                val commandList = groupCommandRegistry.getAllCommands().joinToString("\n") {
                    val firstLine = "${it.command} ${it.name}"
                    it.desc?.let { desc -> "$firstLine\n  $desc" } ?: firstLine
                }
                event.group.sendMessage("当前支持功能:\n$commandList")
            }
        }
    }

}