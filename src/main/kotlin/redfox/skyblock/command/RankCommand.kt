package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.data.Database
import redfox.skyblock.form.general.RankForm
import redfox.skyblock.utils.RankUtil
import redfox.skyblock.utils.Utils

class RankCommand : Command(
    "rutbe",
    "Rütbe menüsü",
    "rutbe",
    arrayOf("rank")
) {
    override fun execute(sender: CommandSender?, commandLabel: String?, args: Array<out String?>?): Boolean {
        if (sender !is Player) return false

        val player = sender
        val currentRank = Database.getRank(player.name)
        val ranks = RankUtil.getRanks()
        val highestRank = ranks.lastOrNull() ?: "Prime"

        if (currentRank == highestRank) {
            player.sendMessage("§8» §cZaten en yüksek rütbeye sahipsin!")
            return true
        }
        RankForm.send(player)
        Utils.sound(sender, "note.harp")

        return true
    }
}
