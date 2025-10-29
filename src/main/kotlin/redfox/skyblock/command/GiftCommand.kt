package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.form.general.GiftForm
import redfox.skyblock.utils.Utils

class GiftCommand : Command(
    "gift",
    "Sunucudaki oyunculara hediye gönderme menüsü.",
    "/hediye",
    arrayOf("hediye")
) {

    override fun execute(sender: CommandSender, commandLabel: String?, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§8» §cBu komutu sadece oyuncular kullanabilir.")
            return false
        }

        val handItem = sender.inventory.itemInHand

        if (handItem.isNull) {
            sender.sendMessage("§8» §cEline bir eşya al.")
            Utils.sound(sender, "item.trident.hit_ground")
            return false
        }

        val onlineCount = sender.server.onlinePlayers.size

        if (onlineCount <= 1) {
            sender.sendMessage("§8» §cSunucuda başka oyuncu yok.")
            Utils.sound(sender, "item.trident.hit_ground")
            return false
        }

        if (handItem.count > 64) {
            sender.close("§cHata Kodu: 418")
        }
        if (sender.gamemode == 0) {
            GiftForm.send(sender, handItem)
            Utils.sound(sender, "note.harp")
        } else sender.sendMessage("§8» §cOyun modunuz 'Survival' olmalı.")

        return true
    }
}
