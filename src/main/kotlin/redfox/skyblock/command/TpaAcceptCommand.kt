package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.*
import redfox.skyblock.manager.TeleportRequestManager

class TpaAcceptCommand : Command("tpak", "Isteği kabul eder") {
    override fun execute(sender: CommandSender, label: String, args: Array<out String>?) =
        (sender as? Player)?.let {
            if (!TeleportRequestManager.accept(it)) it.sendMessage("§8» §cBekleyen isteğin yok.")
            true
        } ?: false
}
