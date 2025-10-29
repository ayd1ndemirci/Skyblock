package redfox.skyblock.command

import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.command.ConsoleCommandSender
import cn.nukkit.command.data.CommandParamType
import cn.nukkit.command.data.CommandParameter
import redfox.skyblock.data.Database
import redfox.skyblock.permission.Permission

class SetMoneyCommand : Command("setmoney", "Oyuncunun parasını ayarlar", "/setmoney <oyuncu> <miktar>") {
    init {
        permission = Permission.ADMIN_MONEY_COMMAND
        permissionMessage = "§cBu komutu yöneticiler kullanabilir."
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
            sender.sendMessage("§8» §cKullanım: /setmoney <oyuncu> <miktar>")
            return false
        }

        val targetName = args[0] ?: return false
        val amount = args[1]?.toIntOrNull()

        if (amount == null || amount < 0) {
            sender.sendMessage("§8» §cGeçerli bir miktar giriniz! (0 veya üzeri)")
            return false
        }

        Database.setMoney(targetName, amount)
        sender.sendMessage("§8» §a$targetName isimli oyuncunun parası $amount olarak ayarlandı.")

        val targetPlayer = sender.server.getPlayerExact(targetName)
        targetPlayer?.sendMessage("§8» §aParanız §2$amount §aolarak ayarlandı!")

        return true
    }
}