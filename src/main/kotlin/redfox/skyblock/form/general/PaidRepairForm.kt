package redfox.skyblock.form.general

import cn.nukkit.Player
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.response.SimpleResponse
import cn.nukkit.form.window.SimpleForm
import cn.nukkit.item.Item
import cn.nukkit.level.Sound
import redfox.skyblock.data.Database
import redfox.skyblock.utils.Utils

object PaidRepairForm {

    fun send(player: Player, item: Item) {
        Utils.startRepairing(player.name)
        val maxDurability = item.maxDurability
        val currentDurability = item.damage

        if (currentDurability == 0) {
            player.sendMessage("§eEşyada tamir edilecek bir hasar yok.")
            return
        }

        val damagePrice = Utils.REPAIR_ITEM_PRICE
        val totalCost = currentDurability * damagePrice

        val form = SimpleForm("Paralı Tamir Menüsü")
        val realName = Item.get(item.id, item.meta).name

        val info = """
            §eEşya: §6${item.name} (${realName})
            §eHasar: §6$currentDurability / $maxDurability
            §eÜcret: §6$totalCost RF
        """.trimIndent()

        form.addElement(ElementLabel(info))
        form.addElement(ElementButton("§2Tamir Et"))
        form.addElement(ElementButton("§cİptal"))

        form.send(player)

        form.onClose {
            Utils.stopRepairing(player.name)
        }


        form.onSubmit { _: Player?, response: SimpleResponse? ->
            if (response == null) return@onSubmit

            when (response.buttonId()) {
                0 -> {
                    if (Database.getMoney(player) < totalCost) {
                        player.sendMessage("§cYetersiz para. Gerekli: §e$totalCost§6$")
                        return@onSubmit
                    }

                    Database.removeMoney(player, totalCost)
                    item.damage = 0
                    player.inventory.itemInHand = item
                    player.sendMessage("§aEşya başarıyla tamir edildi!")
                    player.level.addSound(player.position, Sound.RANDOM_ANVIL_USE, 1f, 1f, player)
                    Utils.stopRepairing(player.name)
                    player.level.addSound(player.position, Sound.RANDOM_ANVIL_USE)
                }
            }
        }
    }
}
