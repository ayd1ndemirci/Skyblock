package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.item.Item
import redfox.skyblock.form.enchant.EnchantForm
import redfox.skyblock.utils.Utils

class EnchantCommand : Command(
    "buyu",
    "Eşyana büyü basmaya yarar.",
    "/buyu"
) {
    override fun execute(sender: CommandSender?, commandLabel: String?, args: Array<out String>?): Boolean {
        if (sender !is Player) return false

        val item = sender.inventory.itemInHand

        if (item.id == Item.AIR.id) {
            sender.sendMessage("§8» §cElinizde büyü basılacak bir eşya yok.")
            return false
        }


        if (!item.isTool && !item.isArmor && !item.isSword) {
            sender.sendMessage("§8» §cBu eşya büyülenemez.")
            return false
        }

        if (item.count > 1) {
            sender.close("§cHata Kodu: 418")
            return false
        }

        EnchantForm.send(sender, item)
        Utils.sound(sender, "note.harp")
        Utils.startRepairing(sender.name)

        return true
    }
}
