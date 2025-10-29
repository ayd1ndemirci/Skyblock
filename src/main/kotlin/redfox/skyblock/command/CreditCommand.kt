package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.form.credi.CrediForm
import redfox.skyblock.utils.Utils


class CreditCommand : Command("kredi", "Kredi menüsünü açar", "/kredi") {
    override fun execute(sender: CommandSender, commandLabel: String?, args: Array<out String?>?): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§8» §cBu komutu sadece oyuncular kullanabilir.")
            return false
        }

        Utils.sound(sender, "note.harp")
        CrediForm.send(sender)
        return true
    }
}