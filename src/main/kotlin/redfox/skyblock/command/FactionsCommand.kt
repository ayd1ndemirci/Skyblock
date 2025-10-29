package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.form.faction.FactionsForm

class FactionsCommand : Command("klan", "Klan komudu", "/klan") {
    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§cBu komutu sadece oyuncular kullanabilir.")
            return false
        }

        FactionsForm.send(sender)
        return true
    }
}
