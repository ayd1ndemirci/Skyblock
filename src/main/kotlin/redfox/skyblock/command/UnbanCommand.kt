package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.utils.TextFormat
import redfox.skyblock.data.Ban
import redfox.skyblock.permission.Permission

class UnbanCommand : Command("unban", "Oyuncunun banını kaldırır") {

    init {
        permission = Permission.BAN_COMMAND
        permissionMessage = "${TextFormat.RED}Bu komutu kullanmaya yetkin yok!"
    }

    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        if (sender is Player && !sender.hasPermission(permission)) {
            sender.sendMessage(permissionMessage)
            return true
        }
        if (args.isEmpty()) {
            sender.sendMessage("§8» §cKullanım: /unban <oyuncu>")
            return true
        }

        val targetName = args.joinToString(" ")

        if (!Ban.isBanned(targetName)) {
            sender.sendMessage("§8» §c$targetName oyuncusu banlı değil.")
            return true
        }

        Ban.unban(targetName)
        sender.sendMessage("§8» §2$targetName §aoyuncusunun banı kaldırıldı.")
        return true
    }
}
