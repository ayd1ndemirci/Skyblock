package redfox.skyblock.form.kit

import cn.nukkit.Player
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.simple.ButtonImage
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.window.SimpleForm
import cn.nukkit.item.Item
import cn.nukkit.item.ItemID
import cn.nukkit.item.enchantment.Enchantment

object StarterKit {

    fun send(player: Player) {
        val message = "§6» §6Merhaba! RedFox Network Factions sunucusuna hoş geldin. Seni burada görmek güzel!"
        val form = SimpleForm("RedFox'a Hoşgeldin")
        form.addElement(ElementLabel(message))
        form.addElement(ElementButton("Başlangıç Kitini Al", ButtonImage(ButtonImage.Type.PATH, "textures/ui/sword")))
        form.send(player)

        form.onSubmit { _, response ->
            if (response == null) return@onSubmit

            when (response.buttonId()) {
                0 -> {
                    val sword = Item.get("stone_sword", 0, 1).setCustomName("Başlangıç Kılıcı")
                    val pickaxe = Item.get("stone_pickaxe", 0, 1).setCustomName("Başlangıç Kazması")
                    val axe = Item.get("stone_axe", 0, 1).setCustomName("Başlangıç Baltası")
                    val shovel = Item.get("stone_shovel", 0, 1).setCustomName("Başlangıç Küreği")
                    val steak = Item.get(ItemID.COOKED_BEEF, 0, 16)
                    sword.addEnchantment(Enchantment.get(Enchantment.ID_DAMAGE_ALL).setLevel(1))
                    sword.addEnchantment(Enchantment.get(Enchantment.ID_DURABILITY).setLevel(1))
                    pickaxe.addEnchantment(Enchantment.get(Enchantment.ID_EFFICIENCY).setLevel(2))
                    axe.addEnchantment(Enchantment.get(Enchantment.ID_EFFICIENCY).setLevel(2))
                    shovel.addEnchantment(Enchantment.get(Enchantment.ID_EFFICIENCY).setLevel(1))

                    val helmet = Item.get(ItemID.LEATHER_HELMET, 0, 1)
                    val chestplate = Item.get(ItemID.LEATHER_CHESTPLATE, 0, 1)
                    val leggings = Item.get(ItemID.LEATHER_LEGGINGS, 0, 1)
                    val boots = Item.get(ItemID.LEATHER_BOOTS, 0, 1)
                    player.inventory.helmet = helmet
                    player.inventory.chestplate = chestplate
                    player.inventory.leggings = leggings
                    player.inventory.boots = boots
                    player.inventory.addItem(sword, pickaxe, axe, shovel, steak)
                }
            }
        }
    }
}