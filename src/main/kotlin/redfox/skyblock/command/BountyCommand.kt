package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.form.bounty.BountyForm

class BountyCommand : Command("kelle", "Oyuncuların kellesine para koymanı sağlar", "/kelle") {
    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§cBu komutu sadece oyuncular kullanabilir.")
            return false
        }

        BountyForm.send(sender)
        return true
    }
}
