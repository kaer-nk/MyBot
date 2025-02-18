package org.example.service

import net.mamoe.mirai.event.events.GroupMessageEvent
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

abstract class GroupCommand {

    abstract val command: String

    abstract val name: String

    abstract val desc: String?

    val client = OkHttpClient().newBuilder()
        .readTimeout(300, TimeUnit.SECONDS)
        .connectTimeout(300, TimeUnit.SECONDS)
        .writeTimeout(300, TimeUnit.SECONDS)
        .callTimeout(300, TimeUnit.SECONDS)
        .build()


    // 执行命令的抽象方法，传入事件和文本参数
    abstract suspend fun execute(event: GroupMessageEvent, word: String)

}
