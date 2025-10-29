package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.form.general.ItemRenameForm
import redfox.skyblock.utils.Utils

class ItemRenameCommand : Command("eisim", "Elindeki eşyanın adını değiştirir", "/eisim") {
    val pricePerItem = 1000

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§8» §cBu komutu sadece oyuncular kullanabilir.")
            return false
        }

        val item = sender.inventory.itemInHand
        if (item.isNull) {
            sender.sendMessage("§8» §cEline bir eşya al.")
            return false
        }

        if (item.count > 1) {
            sender.sendMessage("§8» §cEşyanın sayısı 1'den fazla olmamalı.")
            return false
        }


        ItemRenameForm.send(sender, item, sender.inventory.heldItemIndex, "")
        Utils.sound(sender, "note.harp")
        return true
    }
}
