package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.form.general.AutoSellForm

class AutoSellCommnad : Command(
    "otosat",
    "Otomatik eşysa satma menüsü",
    "/otosat",
    arrayOf("autosell")
) {

    override fun execute(sender: CommandSender?, commandLabel: String?, args: Array<out String?>?): Boolean {
        if (sender !is Player) return false

        AutoSellForm.send(sender)
        return true
    }
}