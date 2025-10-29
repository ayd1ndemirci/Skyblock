package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.form.general.CreditsForm
import redfox.skyblock.utils.Utils

class CreditsCommand : Command(
    "credits",
    "Sunucu yapımcılarını gösterir",
    "/team",
    arrayOf("yapimcilar")
) {

    override fun execute(sender: CommandSender?, commandLabel: String?, args: Array<out String?>?): Boolean {
        if (sender !is Player) return false

        CreditsForm.send(sender)
        Utils.sound(sender, "note.harp")
        return true
    }
}