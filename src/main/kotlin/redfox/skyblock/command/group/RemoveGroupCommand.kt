package redfox.skyblock.command.group

import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.command.data.CommandParamType
import cn.nukkit.command.data.CommandParameter
import redfox.skyblock.enums.Process
import redfox.skyblock.group.GroupManager.removeGroup

class RemoveGroupCommand : Command("removegroup", "Grup siler") {

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

        when (removeGroup(strings[0])) {
            Process.SUCCESS -> commandSender.sendMessage("§u${strings[0]} §rgrubu grup listesinden başarıyla kaldırıldı.")

            Process.NOT_FOUND -> commandSender.sendMessage("§u${strings[0]} §rgrubu bulunamadı.")

            Process.INVALID_NAME -> commandSender.sendMessage("Geçersiz grup adı. §u${strings[0]}")

            else -> commandSender.sendMessage("Bir hata oluştu. Lütfen tekrar deneyin.")
        }

        return true
    }
}