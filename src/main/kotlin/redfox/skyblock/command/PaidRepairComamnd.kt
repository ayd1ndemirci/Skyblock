package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.form.general.PaidRepairForm
import redfox.skyblock.utils.Utils

class PaidRepairComamnd : Command(
    "ptamir",
    "Paralı tamir"
) {

    override fun execute(sender: CommandSender?, commandLabel: String?, args: Array<out String?>?): Boolean {
        if (sender !is Player) return false

        val item = sender.inventory.itemInHand
        if (item.isNull) {
            sender.sendMessage("§8» §cElinizde tamir edilecek bir eşya yok.")
            return false
        }

        if (item.isUnbreakable) {
            sender.sendMessage("§8» §cBu eşya tamir edilemez çünkü kırılmaz.")
            return false
        }

        if (item.damage == 0) {
            sender.sendMessage("§8» §cBu eşya hasar görmemiş.")
            return false
        }
        if (item.count > 1) {
            sender.kick("§cHata Mesajı: 418")
            return false
        }
        Utils.startRepairing(sender.name)
        PaidRepairForm.send(sender, item)
        Utils.sound(sender, "note.harp")
        return true
    }
}