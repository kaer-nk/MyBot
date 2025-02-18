package org.example.group

import org.example.service.GroupCommand


class GroupCommandRegistry() {

    // 使用 Map 将命令标识与命令对象关联
    private val commands: MutableMap<String, GroupCommand> = mutableMapOf()

    fun addCommand(command: GroupCommand) {
        commands[command.command] = command
    }

    // 根据命令标识获取对应的命令对象
    fun getCommand(command: String): GroupCommand? {
        return commands[command]
    }

    // 获取所有支持的命令，用于展示功能列表
    fun getAllCommands(): Collection<GroupCommand> {
        return commands.values
    }

}