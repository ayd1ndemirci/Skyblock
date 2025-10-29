package redfox.skyblock.command

import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.command.ConsoleCommandSender
import cn.nukkit.command.data.CommandParamType
import cn.nukkit.command.data.CommandParameter
import redfox.skyblock.data.Database
import redfox.skyblock.permission.Permission

class SeeCrediCommand : Command("seecredi", "Başkasının kredisine bakar", "/seecredi <oyuncu>") {
    init {
        permission = Permission.ADMIN_CREDI_COMMAND
        permissionMessage = "§cBu komutu yöneticiler kullanabilir."
        commandParameters["default"] = arrayOf(
            CommandParameter.newType("player", true, CommandParamType.TARGET)
        )
    }


    override fun execute(sender: CommandSender, commandLabel: String?, args: Array<out String?>?): Boolean {
        if (sender !is ConsoleCommandSender && !sender.hasPermission(permission)) {
            sender.sendMessage(permissionMessage)
            return true
        }
        if (args == null || args.isEmpty()) {
            sender.sendMessage("§8» §cKullanım: /seecredi <oyuncu>")
            return false
        }

        val targetName = args[0] ?: return false

        val credit = Database.getCredi(targetName)
        sender.sendMessage("§8» §b$targetName §3isimli oyuncunun §b$credit §3kredisi var")
        return true
    }
}