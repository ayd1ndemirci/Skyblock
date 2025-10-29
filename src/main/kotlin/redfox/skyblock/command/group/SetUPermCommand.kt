package redfox.skyblock.command.group

import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.command.data.CommandParamType
import cn.nukkit.command.data.CommandParameter
import redfox.skyblock.data.Tag
import redfox.skyblock.enums.Process
import redfox.skyblock.manager.PermissionManager.addPermission
import java.util.*

class SetUPermCommand : Command("setuperm", "setuperm") {

    init {
        permission = "nukkit.command.op.give"
        commandParameters.clear()
        commandParameters["default"] = arrayOf(
            CommandParameter.newType("player", CommandParamType.TARGET),
            CommandParameter.newType("permission", CommandParamType.STRING)
        )
    }

    override fun execute(commandSender: CommandSender, string: String, strings: Array<String>): Boolean {
        if (!testPermission(commandSender)) return false

        if (strings.size != 2) {
            commandSender.sendMessage(usage)
            return false
        }

        if (!Tag.profileExists(strings[0])) Tag.createProfile(strings[0])

        val process = addPermission(strings[0], strings[1].lowercase(Locale.getDefault()))

        when (process) {
            Process.SUCCESS -> commandSender.sendMessage("§u${strings[1]} §rizin §u${strings[0]} §rüzerine başarıyla eklendi.")
            Process.ALREADY_EXISTS -> commandSender.sendMessage("§u${strings[1]} §rizin §u${strings[0]} §rüzerinde zaten mevcut.")
            else -> commandSender.sendMessage("Bir hata oluştu. Lütfen tekrar deneyin.")
        }


        return true
    }
}
