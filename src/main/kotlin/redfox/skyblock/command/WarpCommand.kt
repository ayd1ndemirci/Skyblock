package redfox.skyblock.command

import WarpForm
import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.utils.Utils

class WarpCommand : Command(
    "warp",
    "Mekan menüsünü açar",
    "/warp",
    arrayOf("mekan")
) {
    override fun execute(sender: CommandSender?, commandLabel: String?, args: Array<out String?>?): Boolean {
        if (sender !is Player) return false

        WarpForm.send(sender)
        Utils.sound(sender, "note.harp")
        return true
    }
}