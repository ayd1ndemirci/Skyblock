package redfox.skyblock.command

import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.command.ConsoleCommandSender
import cn.nukkit.command.data.CommandParamType
import cn.nukkit.command.data.CommandParameter
import redfox.skyblock.data.Database
import redfox.skyblock.permission.Permission

class SeeMoneyCommand : Command("seemoney", "Başkasının parasına bakar", "/seemoney <oyuncu>") {
    init {
        permission = Permission.ADMIN_MONEY_COMMAND
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
            sender.sendMessage("§8» §cKullanım: /seemoney <oyuncu>")
            return false
        }

        val targetName = args[0] ?: return false

        val money = Database.getMoney(targetName)
        sender.sendMessage("§8» §2§o$targetName §risimli oyuncunun §2${money} §aparası var")
        return true
    }
}