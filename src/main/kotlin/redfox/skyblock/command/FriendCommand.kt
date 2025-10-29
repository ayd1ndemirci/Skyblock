package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.form.friend.FriendForm
import redfox.skyblock.utils.Utils

class FriendCommand : Command(
    "arkadas",
    "Sosyalleşme zamanı gençler"
) {

    override fun execute(sender: CommandSender?, commandLabel: String?, args: Array<out String?>?): Boolean {
        if (sender !is Player) return false

        FriendForm.send(sender)
        Utils.sound(sender, "note.harp")
        return true
    }
}