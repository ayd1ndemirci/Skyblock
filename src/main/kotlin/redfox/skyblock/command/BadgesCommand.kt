package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.form.badges.BadgesForm

class BadgesCommand : Command(
    "badges",
    "Rozetler menüsü",
    "badges",
    arrayOf("rozetler")
) {
    override fun execute(sender: CommandSender?, commandLabel: String?, args: Array<out String?>?): Boolean {
        if (sender !is Player) return false
        BadgesForm.send(sender)
        return true
    }
}