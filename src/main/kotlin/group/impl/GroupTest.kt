package org.example.group.impl

import org.example.group.GroupCommandRegistry
import org.example.group.GroupService
import org.example.service.impl.DeepSeekClient
import org.example.service.impl.StardewValleySVESearch
import org.example.service.impl.StardewValleySearch

object GroupTest : GroupService {

    override val groupCommandRegistry = GroupCommandRegistry()

    init {
        groupCommandRegistry.addCommand(StardewValleySearch)
        groupCommandRegistry.addCommand(StardewValleySVESearch)
        groupCommandRegistry.addCommand(DeepSeekClient)
    }

}