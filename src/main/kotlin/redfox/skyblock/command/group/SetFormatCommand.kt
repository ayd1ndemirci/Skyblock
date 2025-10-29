package redfox.skyblock.command.group


import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.command.data.CommandParamType
import cn.nukkit.command.data.CommandParameter
import redfox.skyblock.group.GroupManager.getGroup
import redfox.skyblock.group.GroupManager.setChatFormat

class SetFormatCommand : Command("setformat", "Set format") {

    init {
        permission = "nukkit.command.op.give"
        commandParameters["default"] =
            arrayOf(
                CommandParameter.newType("group", CommandParamType.STRING),
                CommandParameter.newType("format", CommandParamType.TEXT)
            )
    }

    override fun execute(commandSender: CommandSender, string: String, strings: Array<String>): Boolean {
        if (!testPermission(commandSender)) return false

        if (strings.size < 2) {
            commandSender.sendMessage(usage)
            return false
        }

        val group = getGroup(strings[0])
        if (group == null) {
            commandSender.sendMessage("§u${strings[0]} §rgrubu bulunamadı.")
            return false
        }

        var chatFormat = StringBuilder()

        for (i in 1 until strings.size) {
            chatFormat.append(strings[i]).append(" ")
        }

        if (chatFormat.isNotEmpty()) {
            chatFormat = StringBuilder(chatFormat.substring(0, chatFormat.length - 1))
        }

        setChatFormat(group, chatFormat.toString())
        commandSender.sendMessage("§u${strings[0]} §rgrubunun sohbet formatını §u${chatFormat} §rolarak ayarladınız.")
        return true
    }
}