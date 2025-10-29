package redfox.skyblock.form.general

import cn.nukkit.Player
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.custom.ElementInput
import cn.nukkit.form.window.CustomForm
import cn.nukkit.item.Item
import cn.nukkit.level.Sound
import cn.nukkit.utils.TextFormat
import redfox.skyblock.data.Database
import redfox.skyblock.utils.Utils

object ItemRenameForm {

    fun send(player: Player, item: Item, heldItemIndex: Int, message: String = "") {

        val handItem = player.inventory.itemInHand

        if (player.inventory.heldItemIndex != heldItemIndex) {
            player.sendMessage("§8» §cEşyayı elinden çıkardın, işlem iptal.")
            Utils.sound(player, "note.bass")
            return
        }
        if (handItem.count > 1) {
            player.sendMessage("§8» §cSayısı 1'den fazla eşyalara özel isim veremezsin.")
            Utils.sound(player, "note.bass")
            return
        }
        if (
            handItem.id != item.id ||
            handItem.name != item.name ||
            handItem.count != item.count ||
            handItem.customName != item.customName ||
            (handItem.namedTag == null && item.namedTag != null) ||
            (handItem.namedTag != null && item.namedTag == null) ||
            (handItem.namedTag != null && item.namedTag != null && handItem.namedTag != item.namedTag) ||
            handItem.enchantments.size != item.enchantments.size ||
            !handItem.enchantments.all { handEnch ->
                item.enchantments.any { itemEnch ->
                    itemEnch.id == handEnch.id && itemEnch.level == handEnch.level
                }
            }
        ) {
            player.sendMessage("§8» §cEşyayı değiştirme len!")
            Utils.sound(player, "item.trident.hit_ground")
            return
        }

        val itemName = TextFormat.clean(item.name.split("\n")[0])
        val fullName = Item.get(item.id, item.meta).name
        val isVip = Utils.isVIP(player)
        val price = if (isVip) 0 else 2500
        val priceText = if (isVip) "Ücretsiz (VIP)" else price.toString()

        val form = CustomForm("Eşyayı Adlandırma Menüsü")
        form.addElement(ElementLabel(message))
        form.addElement(ElementLabel("§3Elindeki Eşya: §f${itemName} (${fullName})§r\n§3Ücret: §f${priceText}\n"))
        form.addElement(ElementInput("", "Eşyanın yeni adı"))
        form.send(player)

        form.onSubmit { _, response ->
            if (response == null) return@onSubmit
            val newName = response.getInputResponse(2)?.trim() ?: ""
            val handItem = player.inventory.itemInHand

            if (newName.isEmpty()) {
                send(player, item, player.inventory.heldItemIndex, "§cEşya ismi gir.")
                Utils.sound(player, "note.bass")
                return@onSubmit
            }

            if (newName.length !in 3..16) {
                send(player, item, player.inventory.heldItemIndex, "§cEşya ismini 3-16 karakter arası girebilirsin.")
                Utils.sound(player, "note.bass")
                return@onSubmit
            }

            if (player.inventory.heldItemIndex != heldItemIndex) {
                player.sendMessage("§8» §cSlot değişme len.")
                Utils.sound(player, "note.bass")
                return@onSubmit
            }
            if (handItem.count > 1) {
                player.sendMessage("§8» §cSayısı 1'den fazla eşyalara özel isim veremezsin.")
                Utils.sound(player, "note.bass")
                return@onSubmit
            }
            if (
                handItem.id != item.id ||
                handItem.name != item.name ||
                handItem.count != item.count ||
                handItem.customName != item.customName ||
                (handItem.namedTag == null && item.namedTag != null) ||
                (handItem.namedTag != null && item.namedTag == null) ||
                (handItem.namedTag != null && item.namedTag != null && handItem.namedTag != item.namedTag) ||
                handItem.enchantments.size != item.enchantments.size ||
                !handItem.enchantments.all { handEnch ->
                    item.enchantments.any { itemEnch ->
                        itemEnch.id == handEnch.id && itemEnch.level == handEnch.level
                    }
                }
            ) {
                player.sendMessage("§8» §cEşyayı değiştirme len!")
                Utils.sound(player, "note.bass")
                return@onSubmit
            }

            if (price > 0) {
                if (Database.getMoney(player) >= price) {
                    Database.removeMoney(player.name, price)
                } else {
                    player.sendMessage("§8» §cYeterli paran yok.")
                    Utils.sound(player, "item.trident.hit_ground")
                    return@onSubmit
                }
            }
            val cleanedName = TextFormat.clean(newName)
            val newItemName = "§r§e${cleanedName}\n§3İmzalayan: §f${player.name}"
            player.inventory.itemInHand = item.clone().setCustomName(newItemName)
            player.sendMessage("§8» §3Eşyanızın ismi §b'${TextFormat.clean(newName)}§b' §3olarak değiştirildi.")
            player.level.addSound(player.position, Sound.RANDOM_ANVIL_USE, 1f, 1f, player)
        }
    }
}