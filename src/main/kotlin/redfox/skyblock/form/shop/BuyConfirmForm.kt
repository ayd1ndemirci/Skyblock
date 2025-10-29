package redfox.skyblock.form.shop

import cn.nukkit.Player
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.window.SimpleForm
import cn.nukkit.item.Item
import redfox.skyblock.data.Database
import redfox.skyblock.manager.ShopItemData
import redfox.skyblock.utils.Utils

object BuyConfirmForm {

    fun send(player: Player, itemData: ShopItemData, amount: Int) {
        val item = Item.get(itemData.id, itemData.meta, amount)

        val basePrice = itemData.price * amount
        val discount = getDiscount(player)
        val discountedPrice = (basePrice - (basePrice * discount)).toInt()
        val playerMoney = Database.getMoney(player)
        val remainingMoney = playerMoney - discountedPrice

        val form = SimpleForm("Market Satın Alma Onayı")
        val message = buildString {
            append("§3Alınacak Eşya: §f${item.name}\n")
            append("§3Miktar: §f$amount\n")
            append("§3Ödenecek Para: §f$discountedPrice")
            if (discount > 0) {
                append(" §7(%${(discount * 100).toInt()} indirim)")
            }
            append("\n§3Kalan Para: §f$remainingMoney\n\n")
            append("§bSatın almayı onaylıyor musun?")
        }

        form.addElement(ElementLabel(message))
        form.addElement(ElementButton("§2Onaylıyorum"))
        form.addElement(ElementButton("§cİptal"))

        form.send(player)

        form.onSubmit { _, response ->
            when (response.buttonId()) {
                0 -> {
                    if (playerMoney < discountedPrice) {
                        player.sendMessage("§cParan yetersiz!")
                        Utils.sound(player, "item.trident.hit_ground")
                        return@onSubmit
                    }

                    if (!player.inventory.canAddItem(item)) {
                        player.sendMessage("§cEnvanterin dolu!")
                        Utils.sound(player, "item.trident.hit_ground")
                        return@onSubmit
                    }

                    Database.removeMoney(player, discountedPrice)
                    player.inventory.addItem(item)
                    player.sendMessage("§a§l${item.count}x ${item.name} §r§abakiyenden §e$discountedPrice RF §akarşılığı satın alındı!")
                    Utils.sound(player, "random.orb")
                }

                1 -> player.sendMessage("§cSatın alma iptal edildi.")
            }
        }
    }

    private fun getDiscount(player: Player): Double {
        return when {
            player.hasPermission("skyblock.shop.discount.mvip") -> 0.30
            player.hasPermission("skyblock.shop.discount.vip+") -> 0.10
            player.hasPermission("skyblock.shop.discount.vip") -> 0.05
            else -> 0.0
        }
    }
}
