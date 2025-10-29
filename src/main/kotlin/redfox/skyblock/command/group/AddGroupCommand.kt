package redfox.skyblock.command.group

import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.command.data.CommandParamType
import cn.nukkit.command.data.CommandParameter
import redfox.skyblock.enums.Process
import redfox.skyblock.group.GroupManager.addGroup

class AddGroupCommand : Command(
    "addgroup",
    "Tag verir"
) {

    init {
        permission = "groupmanager.add_group"
        commandParameters.clear()
        commandParameters["default"] = arrayOf(CommandParameter.newType("group", CommandParamType.STRING))
    }

    override fun execute(commandSender: CommandSender, string: String, strings: Array<String>): Boolean {
        if (!testPermission(commandSender)) return false

        if (strings.size != 1) {
            commandSender.sendMessage(usage)
            return false
        }

        when (addGroup(strings[0])) {
            Process.SUCCESS -> commandSender.sendMessage("§u${strings[0]} §rgrup listesine başarıyla eklendi.")
            Process.ALREADY_EXISTS -> commandSender.sendMessage("§u${strings[0]} §rgrubu zaten mevcut.")
            Process.INVALID_NAME -> commandSender.sendMessage("Geçersiz grup adı. §u${strings[0]}")
            else -> commandSender.sendMessage("Bir hata oluştu. Lütfen tekrar deneyin.")
        }

        return true
    }

}