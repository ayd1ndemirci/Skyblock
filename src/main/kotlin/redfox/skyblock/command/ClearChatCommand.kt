package redfox.skyblock.command

import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.utils.Utils

class ClearChatCommand : Command("cc", "Sohbeti temizler", "/cc", arrayOf("cc")) {
    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        if (!Utils.isAdmin(sender)) {
            sender.sendMessage("§8» §cBu komutu sadece yöneticiler kullanabilir.")
            return false
        }

        for (i in 1..100) {
            sender.server.broadcastMessage("§f")
        }
        sender.server.broadcastMessage("§aSohbet temizlendi.")
        sender.server.levels.values.forEach { level ->
            level.players.values.forEach { onlinePlayer ->
                Utils.sound(onlinePlayer, "note.pling")
            }
        }
        return true
    }
}
