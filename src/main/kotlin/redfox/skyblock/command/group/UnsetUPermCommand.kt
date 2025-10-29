package redfox.skyblock.command.group

import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.command.data.CommandParamType
import cn.nukkit.command.data.CommandParameter
import redfox.skyblock.data.Tag
import redfox.skyblock.enums.Process
import redfox.skyblock.manager.PermissionManager.removePermission
import java.util.*

class UnsetUPermCommand : Command("unset", "unsetuperm") {

    init {
        permission = "nukkit.command.op.give"
        commandParameters.clear()
        commandParameters["default"] =
            arrayOf(
                CommandParameter.newType("oyuncu", CommandParamType.TARGET),
                CommandParameter.newType("izin", CommandParamType.STRING)
            )
    }

    override fun execute(commandSender: CommandSender, string: String, strings: Array<String>): Boolean {
        if (!testPermission(commandSender)) return false

        if (strings.size != 2) {
            commandSender.sendMessage("Kullanım: /unset <oyuncu> <izin>")
            return false
        }

        if (Tag.profileExists(strings[0])) Tag.createProfile(strings[0])

        val process = removePermission(strings[0], strings[1].lowercase(Locale.getDefault()))
        when (process) {
            Process.SUCCESS -> commandSender.sendMessage(
                "§aBaşarılı: §f${strings[0]} oyuncusundan §e${strings[1]} §fizni kaldırıldı."
            )

            Process.NOT_FOUND -> commandSender.sendMessage(
                "§cHata: §f${strings[0]} oyuncusunda §e${strings[1]} §fizni bulunamadı."
            )

            else -> commandSender.sendMessage("§cBir hata oluştu. Lütfen tekrar deneyin.")
        }
        return true
    }
}
