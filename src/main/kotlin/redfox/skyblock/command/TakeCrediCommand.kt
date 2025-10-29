package redfox.skyblock.command

import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.command.ConsoleCommandSender
import cn.nukkit.command.data.CommandParamType
import cn.nukkit.command.data.CommandParameter
import redfox.skyblock.data.Database
import redfox.skyblock.permission.Permission

class TakeCrediCommand : Command("takecredi", "Oyuncudan kredi alır", "/takecredi <oyuncu> <miktar>") {
    init {
        permission = Permission.ADMIN_CREDI_COMMAND
        permissionMessage = "§cBu komutu yöneticiler kullanabilir."
    }

    init {
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
            sender.sendMessage("§8» §cKullanım: /takecredi <oyuncu> <miktar>")
            return false
        }

        val targetName = args[0] ?: return false
        val amount = args[1]?.toIntOrNull()

        if (amount == null || amount <= 0) {
            sender.sendMessage("§8» §cGeçerli bir miktar giriniz!")
            return false
        }

        if (!Database.hasCredi(targetName, amount)) {
            sender.sendMessage("§8» §c$targetName yeterli krediye sahip değil!")
            return false
        }

        Database.removeCredi(targetName, amount)
        sender.sendMessage("§8» §2$amount §akredi §2$targetName §aisimli oyuncudan alındı.")

        val targetPlayer = sender.server.getPlayerExact(targetName)
        targetPlayer?.sendMessage("§8» §4$amount §ckredi hesabından düşüldü!")

        return true
    }
}