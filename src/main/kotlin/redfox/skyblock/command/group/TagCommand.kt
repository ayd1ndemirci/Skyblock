package redfox.skyblock.command.group

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.form.general.TagForm

class TagCommand : Command(
    "tag",
    "Taglar arası geçiş yapabilirsin"
) {

    override fun execute(sender: CommandSender?, commandLabel: String?, args: Array<out String?>?): Boolean {
        if (sender !is Player) return false
        TagForm.send(sender)
        return true
    }
}