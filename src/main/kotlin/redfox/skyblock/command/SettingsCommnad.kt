package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.form.settings.SettingsForm
import redfox.skyblock.utils.Utils

class SettingsCommnad : Command(
    "settings",
    "Ayarlar menüsünü açar.",
    "/ayarlar", arrayOf("ayarlar")
) {
    override fun execute(sender: CommandSender?, commandLabel: String?, args: Array<out String?>?): Boolean {
        if (sender !is Player) return false

        SettingsForm.send(sender)
        Utils.sound(sender, "note.harp")
        return true
    }
}