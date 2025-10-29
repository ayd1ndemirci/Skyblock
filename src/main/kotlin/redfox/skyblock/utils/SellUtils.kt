package redfox.skyblock.utils

import cn.nukkit.Player
import cn.nukkit.level.Sound
import cn.nukkit.utils.TextFormat
import redfox.skyblock.data.Database

object SellUtils {

    fun sellHand(player: Player) {
        val item = player.inventory.itemInHand
        val pricePerItem = SellDataLoader.getPrice(item)

        if (pricePerItem > 0) {
            val total = pricePerItem * item.count
            player.inventory.removeItem(item)
            player.sendMessage("${TextFormat.GREEN}${item.name} x${item.count} satıldı! +$total RF")
            player.level.addSound(player.position, Sound.RANDOM_POP, 1f, 1f, player)
            Database.addMoney(player, total)
        } else {
            player.sendMessage("${TextFormat.RED}Elindeki item satılamaz!")
            player.level.addSound(player.position, Sound.NOTE_HAT, 1f, 1f, player)
        }
    }

    fun sellAll(player: Player) {
        var total = 0
        val inventory = player.inventory
        val contents = inventory.contents

        for ((slot, item) in contents) {
            val pricePer = SellDataLoader.getPrice(item)
            if (pricePer > 0) {
                total += pricePer * item.count
                inventory.clear(slot)
            }
        }

        if (total > 0) {
            Database.addMoney(player, total)
            player.sendMessage("${TextFormat.GREEN}Tüm satılabilir eşyalar satıldı! Kazanç: $total RF")
            player.level.addSound(player.position, Sound.MOB_BLAZE_SHOOT, 1f, 1f, player)
        } else {
            player.sendMessage("${TextFormat.RED}Satılabilir eşya bulunamadı.")
            player.level.addSound(player.position, Sound.NOTE_HAT, 1f, 1f, player)
        }
    }
}
