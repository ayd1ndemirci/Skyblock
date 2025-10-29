package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.utils.Utils

class DiscordCommand : Command(
    "discord",
    "Discord sunucumuzun davet linkini gösterir."
) {

    override fun execute(sender: CommandSender?, commandLabel: String?, args: Array<out String?>?): Boolean {
        sender?.sendMessage("§7--------------------\n§7*\n§r§dLink: §b§bredfoxmc.com/discord§r\n§7*\n§7--------------------")
        Utils.sound(sender as Player, "note.pling")
        return true
    }
}