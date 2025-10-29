package redfox.skyblock.command.group


import cn.nukkit.Server
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.command.data.CommandEnum
import cn.nukkit.command.data.CommandParamType
import cn.nukkit.command.data.CommandParameter
import redfox.skyblock.event.custom.PlayerGroupChangeEvent
import redfox.skyblock.group.GroupManager
import redfox.skyblock.group.GroupManager.getGroup
import redfox.skyblock.group.GroupManager.setPlayerGroup

class SetGroupCommand : Command("setgroup", "Set group") {

    init {
        permission = "nukkit.command.op.give"
        commandParameters.clear()
        commandParameters["default"] =
            arrayOf(
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newEnum("group", CommandEnum("group", GroupManager.getGroupIds()))
            )
    }

    override fun execute(commandSender: CommandSender, string: String, strings: Array<String>): Boolean {
        if (!testPermission(commandSender)) return false

        if (strings.size != 2) {
            commandSender.sendMessage(usage)
            return false
        }

        val group = getGroup(strings[1])
        if (group == null) {
            commandSender.sendMessage("§u${strings[1]} §rgrubu bulunamadı.")
            return false
        }

        var name = strings[0]
        val player = Server.getInstance().getPlayer(strings[0])
        if (player != null) {
            val event = PlayerGroupChangeEvent(player, group, commandSender)
            Server.getInstance().pluginManager.callEvent(event)
            name = player.name
        }

        setPlayerGroup(name, group)
        commandSender.sendMessage("§u$name §rkişisine §u${group.name} §rgrubu başarıyla atandı.")
        return true

    }
}