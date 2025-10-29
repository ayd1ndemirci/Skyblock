package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.*
import redfox.skyblock.manager.TeleportRequestManager

class TpaDenyCommand : Command("tpar", "Isteği reddeder") {
    override fun execute(sender: CommandSender, label: String, args: Array<out String>?) =
        (sender as? Player)?.let {
            if (!TeleportRequestManager.deny(it)) it.sendMessage("§8» §cBekleyen isteğin yok.")
            true
        } ?: false
}
