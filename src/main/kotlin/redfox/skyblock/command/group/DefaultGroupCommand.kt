package redfox.skyblock.command.group

import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.command.data.CommandParamType
import cn.nukkit.command.data.CommandParameter
import redfox.skyblock.group.GroupManager.getGroup
import redfox.skyblock.group.GroupManager.setDefaultGroup

class DefaultGroupCommand : Command(
    "defaultgroup",
    "Default group"
) {

    init {
        permission = "nukkit.command.op.give"
        commandParameters.clear()
        commandParameters["default"] = arrayOf(CommandParameter.newType("group", CommandParamType.STRING))
    }

    override fun execute(commandSender: CommandSender, string: String, strings: Array<String>): Boolean {
        if (!testPermission(commandSender)) return false
        if (strings.size != 1) {
            commandSender.sendMessage(usage)
            return false
        }

        val group = getGroup(strings[0])
        if (group == null) {
            commandSender.sendMessage("§u${strings[0]} §rgrubu bulunamadı.")
            return false
        }

        setDefaultGroup(group)
        commandSender.sendMessage("Varsayılan grup başarıyla §u${group.name} §rolarak ayarlandı.")
        return true
    }
}