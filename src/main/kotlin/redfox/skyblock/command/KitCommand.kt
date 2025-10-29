package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.form.kit.KitForm
import redfox.skyblock.utils.Utils

class KitCommand : Command(
    "kit",
    "Kit komutu",
    "/kit"
) {
    override fun execute(sender: CommandSender?, commandLabel: String?, args: Array<out String?>?): Boolean {
        if (sender !is Player) return false
        KitForm.send(sender)
        Utils.sound(sender, "note.harp")
        return true
    }
}