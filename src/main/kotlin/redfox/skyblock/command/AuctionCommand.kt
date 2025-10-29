package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.form.auction.AuctionForm

class AuctionCommand : Command(
    "ihale",
    "İhale menüsü",
    "/auction",
    arrayOf("ac")
) {
    override fun execute(sender: CommandSender?, commandLabel: String?, args: Array<out String?>?): Boolean {
        if (sender !is Player) return false

        AuctionForm.send(sender)
        return true
    }
}