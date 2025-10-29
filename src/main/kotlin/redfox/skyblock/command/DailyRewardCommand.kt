package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.form.general.DailyRewardForm
import redfox.skyblock.service.RewardService
import redfox.skyblock.utils.Utils

class DailyRewardCommand : Command("gunlukodul", "Günlük ödül menüsü.") {

    init {
        this.setAliases(arrayOf("go"))
    }

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false

        if (RewardService.canClaim(sender)) {
            DailyRewardForm.send(sender)
            Utils.sound(sender, "note.harp")
        } else {
            sender.sendMessage("§8» §cGünlük ödülünü zaten almışsın.")
        }
        return true
    }
}
