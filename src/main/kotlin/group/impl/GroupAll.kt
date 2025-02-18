package org.example.group.impl

import org.example.group.GroupCommandRegistry
import org.example.group.GroupService
import org.example.service.impl.*

//test
//让机器人在不同的群注册不同的功能
object GroupAll : GroupService {

    override val groupCommandRegistry = GroupCommandRegistry()

    init {
        groupCommandRegistry.addCommand(StardewValleySearch)
        groupCommandRegistry.addCommand(StardewValleySVESearch)
        groupCommandRegistry.addCommand(TrSearch)
        groupCommandRegistry.addCommand(TrCalamitySearch)
        groupCommandRegistry.addCommand(DeepSeekClient)
        groupCommandRegistry.addCommand(OpenAIClient)
    }

}