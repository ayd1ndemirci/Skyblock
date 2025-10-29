package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.form.general.ListForm
import redfox.skyblock.utils.Utils

class ListCommand : Command(
    "list",
    "Aktif oyuncu listesini görüntüler",
    "/list",
    arrayOf("aktifler")
) {
    override fun execute(sender: CommandSender?, commandLabel: String?, args: Array<out String?>?): Boolean {
        if (sender !is Player) return false
        ListForm.send(sender)
        Utils.sound(sender, "note.harp")
        return true
    }
}