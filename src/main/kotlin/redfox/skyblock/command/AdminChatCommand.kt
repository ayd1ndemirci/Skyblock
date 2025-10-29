package redfox.factions.command

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.command.ConsoleCommandSender
import cn.nukkit.utils.TextFormat
import redfox.skyblock.Core
import redfox.skyblock.permission.Permission

class AdminChatCommand : Command(
    "ys",
    "Yetkili sohbet komutu (/ys mesaj)",
    "/ys <mesaj>"
) {
    init {
        permission = Permission.SC_COMMAND
        permissionMessage = "${TextFormat.RED}Admin değilsin kanki :("
    }

    override fun execute(sender: CommandSender?, commandLabel: String?, args: Array<out String?>?): Boolean {
        if (sender == null) {
            return false
        }
        if (args == null || args.isEmpty() || args.joinToString(" ").isBlank()) {
            sender.sendMessage("${TextFormat.RED}Kullanım: /ys <mesaj>")
            return true
        }

        if (sender is Player && !sender.hasPermission(permission) || !sender.isOp) {
            sender.sendMessage(permissionMessage)
            return true
        }

        val message = args.joinToString(" ")

        val senderName = when (sender) {
            is ConsoleCommandSender -> "Konsol"
            else -> sender.name
        }

        val formattedMessage = "${TextFormat.RED}[YS] $senderName: ${TextFormat.WHITE}$message"

        Server.getInstance().onlinePlayers.values
            .filter { it.hasPermission(permission) }
            .forEach { it.sendMessage(formattedMessage) }

        Core.instance.logger.info(formattedMessage)

        return true
    }
}
