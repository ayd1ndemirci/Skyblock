package redfox.skyblock.command.group

import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.group.GroupManager

class ListGroupCommand : Command("listgroups", "List grup") {

    init {
        aliases = arrayOf("groups")
        permission = "nukkit.command.op.give"
    }

    override fun execute(commandSender: CommandSender, string: String, strings: Array<String>): Boolean {
        if (!testPermission(commandSender)) return false

        val groups = StringBuilder()
        for ((name) in GroupManager.groups) groups.append(name).append(", ")

        groups.delete(groups.length - 2, groups.length)
        commandSender.sendMessage("Tüm kayıtlı gruplar: $groups")
        return true
    }
}