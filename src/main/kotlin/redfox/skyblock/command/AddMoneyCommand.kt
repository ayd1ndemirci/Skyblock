package redfox.skyblock.command

import cn.nukkit.Server
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.command.ConsoleCommandSender
import cn.nukkit.command.data.CommandParamType
import cn.nukkit.command.data.CommandParameter
import redfox.skyblock.data.Database
import redfox.skyblock.permission.Permission

class AddMoneyCommand : Command("addmoney", "Oyuncuya para ekler", "/addmoney <oyuncu> <miktar>") {
    init {
        permission = Permission.ADMIN_MONEY_COMMAND
        permissionMessage = "§cBu komutu yöneticiler kullanabilir."
        commandParameters.clear()
        commandParameters["default"] = arrayOf(
            CommandParameter.newType("player", true, CommandParamType.TARGET),
            CommandParameter.newType("amount", true, CommandParamType.INT),
        )
    }

    override fun execute(sender: CommandSender, commandLabel: String?, args: Array<out String?>?): Boolean {
        if (sender !is ConsoleCommandSender && !sender.hasPermission(permission)) {
            sender.sendMessage(permissionMessage)
            return true
        }

        if (args == null || args.size < 2) {
            sender.sendMessage("§8» §cKullanım: /addmoney <oyuncu> <miktar>")
            return false
        }

        val targetName = args[0] ?: return false
        val amount = args[1]?.toIntOrNull()

        if (amount == null || amount <= 0) {
            sender.sendMessage("§8» §cGeçerli bir miktar giriniz!")
            return false
        }

        val targetPlayer = Server.getInstance().getPlayerExact(targetName)

        if (targetPlayer == null) {
            Database.addMoney(targetName, amount)
        } else Database.addMoney(targetPlayer, amount)

        sender.sendMessage("§8» §b$targetName §3isimli oyuncuya §b$amount §3para eklendi.")

        return true
    }
}