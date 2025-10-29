package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.utils.Utils

class VIPInfoCommand : Command(
    "vipbilgi",
    "Ücretli üyelikler özelliklerini görme menüsü"
) {

    override fun execute(sender: CommandSender?, commandLabel: String?, args: Array<out String?>?): Boolean {
        if (sender !is Player) return false
        Utils.sound(sender, "note.harp")
        return true
    }
}