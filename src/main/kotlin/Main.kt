package org.example

import ConfigManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.utils.BotConfiguration
import org.example.group.impl.GroupAll

suspend fun main() {

    val qq = ConfigManager.CONFIG.getLong("qq")
    val password = ConfigManager.CONFIG.getString("password")
    val groupIds = ConfigManager.CONFIG.getJSONArray("useAllGroupIds").map { (it as Int).toLong() }

    val bot = BotFactory.newBot(qq,password) {
        protocol = BotConfiguration.MiraiProtocol.MACOS
        fileBasedDeviceInfo()
    }

    bot.login()

    bot.globalEventChannel().subscribeAlways<GroupMessageEvent>{ event ->

        if (groupIds.contains(event.group.id)) {
            CoroutineScope(Dispatchers.IO).launch {
                GroupAll.invoke(event)
            }
        }


        //不同的群可以注册不同的功能使用
//        if (groupIds.contains(11L)) {
//            CoroutineScope(Dispatchers.IO).launch {
////                GroupTest.invoke(event)
//            }
//        }

    }

}