package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.form.general.HelpForm
import redfox.skyblock.utils.Utils

class HelpCommand : Command(
    "yardim",
    "Sunucu hakkında bilmediğin tüm bilgileri buradan okuyabilirsin"
) {

    override fun execute(sender: CommandSender?, commandLabel: String?, args: Array<out String?>?): Boolean {
        if (sender !is Player) return false
        HelpForm.send(sender)
        Utils.sound(sender, "note.harp")
        return true
    }
}