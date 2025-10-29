package redfox.skyblock.command

import cn.nukkit.Server
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.command.ConsoleCommandSender
import cn.nukkit.command.data.CommandParamType
import cn.nukkit.command.data.CommandParameter
import cn.nukkit.utils.TextFormat
import redfox.skyblock.data.Database
import redfox.skyblock.model.HistoryRecord
import redfox.skyblock.permission.Permission

class AddCreditCommand : Command("krediekle", "Oyuncuya kredi ekler", "/krediekle <oyuncu> <miktar>") {
    init {
        permission = Permission.ADMIN_CREDI_COMMAND
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
            sender.sendMessage("§8» §cKullanım: /addcredi <oyuncu> <miktar>")
            sender.sendMessage("${TextFormat.RED}Kullanım: /krediekle <oyuncu> <miktar>")
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
            Database.addCredi(targetName, amount)
        } else Database.addCredi(targetPlayer.name, amount)

        sender.sendMessage("§8» §b$targetName §3isimli oyuncuya §b$amount §3kredi eklendi.")
        targetPlayer?.sendMessage("§8» §b$amount §3kredi hesabına eklendi!")

        val reason = if (sender is ConsoleCommandSender) {
            "§aKredi Alındı§7 <- Satın alım"
        } else {
            "§aKredi Alındı§7 <- ${sender.name} §8(admin)"
        }

        Database.addCrediRecord(
            targetName,
            HistoryRecord(
                name = targetName,
                action = reason,
                amount = amount
            )
        )

        return true
    }
}