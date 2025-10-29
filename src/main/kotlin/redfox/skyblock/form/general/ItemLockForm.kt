package redfox.skyblock.form.general

import cn.nukkit.Player
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.simple.ButtonImage
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.response.SimpleResponse
import cn.nukkit.form.window.SimpleForm
import cn.nukkit.item.Item
import redfox.skyblock.utils.Utils

object ItemLockForm {

    fun send(player: Player, item: Item) {
        val itemName = Item.get(item.id)
        val form = SimpleForm("Eşya Kilit Menüsü")

       if (item.itemLockMode.name == "NONE") {
           form.addElement(ElementLabel("§aKilitlenecek eşyanın bilgileri aşağıya listelendi"))
           form.addElement(ElementLabel("§3Eşya: §f${itemName.name} (${item.damage}/${item.maxDurability})\n§3Büyüleri: §f${item.enchantments.joinToString { ench -> "${ench.name} ${ench.level}" }}\n"))
           form.addElement(ElementButton("Kilitle", ButtonImage(ButtonImage.Type.PATH, "textures/ui/lock_color.png")))

       } else {
           form.addElement(ElementLabel("§aKilidi açılacak eşyanın bilgileri listelendi"))
           form.addElement(ElementLabel("§3Eşya: §f${itemName.name} (${item.damage}/${item.maxDurability})\n§3Büyüleri: §f${item.enchantments.joinToString { ench -> "${ench.name} ${ench.level}" }}\n"))
           form.addElement(ElementButton("Kilidi Aç", ButtonImage(ButtonImage.Type.PATH, "textures/ui/icon_unlocked.png")))
       }
        form.send(player)

        form.onSubmit { _, response: SimpleResponse ->
            val hand = player.inventory.itemInHand
            if (item != hand) {
                player.sendMessage("§8» §cEşyayı değiştirme birader.")
                Utils.sound(player, "item.trident.hit_ground")
                return@onSubmit
            }
            when (response.button().text()) {
                "Kilitle" -> {

                    val itemInHand = player.inventory.itemInHand
                    itemInHand.setItemLockMode(Item.ItemLockMode.LOCK_IN_INVENTORY)
                    player.inventory.itemInHand = itemInHand.clone()
                    Utils.sound(player, "note.pling")
                    player.sendMessage("§8» §aElindeki eşyayı kilitledin.")
                }
                "Kilidi Aç" -> {
                    val itemInHand = player.inventory.itemInHand
                    itemInHand.setItemLockMode(Item.ItemLockMode.NONE)
                    player.inventory.itemInHand = itemInHand.clone()
                    Utils.sound(player, "note.pling")
                    player.sendMessage("§8» §aElindeki eşyanın kilidini açtın.")
                }
            }
        }
    }
}