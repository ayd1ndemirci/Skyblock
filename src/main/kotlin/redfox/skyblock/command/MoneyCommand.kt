package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.form.money.MoneyForm
import redfox.skyblock.utils.Utils

class MoneyCommand : Command("para", "Para yönetimi", "/para") {
    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§8» §cBu komutu sadece oyuncular kullanabilir.")
            return false
        }
        MoneyForm.send(sender)
        Utils.sound(sender, "note.harp")
        return true
    }
}
