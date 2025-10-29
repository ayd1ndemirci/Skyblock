package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.utils.Location
import redfox.skyblock.utils.Utils

class LobbyCommand : Command(
    "lobi",
    "Lobiye ışınlanma komutu"
) {

    override fun execute(sender: CommandSender?, commandLabel: String?, args: Array<out String?>?): Boolean {
        if (sender !is Player) return false

        Location.teleportWorld(sender, sender.server.defaultLevel.folderName)
        Utils.sound(sender, "note.pling")
        return true
    }
}