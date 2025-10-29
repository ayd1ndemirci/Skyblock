package redfox.skyblock.form.enchant

import cn.nukkit.Player
import cn.nukkit.form.element.custom.ElementDropdown
import cn.nukkit.form.element.custom.ElementSlider
import cn.nukkit.form.window.CustomForm
import cn.nukkit.item.Item
import cn.nukkit.level.Sound
import redfox.skyblock.data.Database
import redfox.skyblock.manager.EnchantmentManager
import redfox.skyblock.utils.Utils

object EnchantForm {

    fun send(player: Player, item: Item) {
        Utils.startRepairing(player.name)

        if (item.isNull) {
            player.sendMessage("§cBüyü basılacak eşya bulunamadı.")
            Utils.stopRepairing(player.name)
            return
        }

        val enchantments = EnchantmentManager.getApplicableEnchantments(item)

        if (enchantments.isEmpty()) {
            player.sendMessage("§cBu eşyaya uygun büyü bulunamadı.")
            return
        }

        val form = CustomForm("Büyü Menüsü")
        val enchantmentNames = enchantments.map { it.name }
        form.addElement(ElementDropdown("§7Bir büyü seçin", enchantmentNames))
        form.addElement(ElementSlider("§7Seviye Seçin", 1f, 5f, 1, 1f))

        form.send(player)

        form.onClose {
            Utils.stopRepairing(player.name)
        }

        form.onSubmit { _, response ->
            if (response == null) return@onSubmit

            val enchantIndex = response.getDropdownResponse(0).elementId()
            val level = response.getSliderResponse(1).toInt()
            val selectedEnchant = enchantments.getOrNull(enchantIndex) ?: return@onSubmit

            val cost = level * 1000

            val money = Database.getMoney(player.name)
            if (money < cost) {
                player.sendMessage("§cYeterli paran yok! Gerekli: $cost")
                Utils.sound(player, "item.trident.hit_ground")
                return@onSubmit
            }

            Database.removeMoney(player.name, cost)

            val newItem = item.clone()
            newItem.addEnchantment(selectedEnchant.setLevel(level))

            player.inventory.itemInHand = newItem
            player.sendMessage("§a${selectedEnchant.name} $level seviyesinde basıldı! §7(-$cost$)")
            player.level.addSound(player.position, Sound.RANDOM_ANVIL_USE, 1f, 1f, player)
            Utils.stopRepairing(player.name)
        }
    }
}
