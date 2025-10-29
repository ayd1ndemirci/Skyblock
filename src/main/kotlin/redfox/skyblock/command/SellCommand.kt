package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.form.sell.SellForm
import redfox.skyblock.utils.Utils

class SellCommand : Command(
    "sat",
    "Eşya satmanı sağlar"
) {

    override fun execute(sender: CommandSender?, commandLabel: String?, args: Array<out String?>?): Boolean {
        if (sender !is Player) return false

        SellForm.send(sender)
        Utils.sound(sender, "note.harp")
        return true
    }
}