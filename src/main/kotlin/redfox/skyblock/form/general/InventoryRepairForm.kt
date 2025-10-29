package redfox.skyblock.form.general

import cn.nukkit.Player
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.simple.ButtonImage
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.window.SimpleForm
import cn.nukkit.item.Item
import cn.nukkit.level.Sound
import redfox.skyblock.utils.Utils

object InventoryRepairForm {

    fun send(player: Player, items: List<Item>) {
        Utils.startRepairing(player.name)
        val damagedItems = items.filter { it.damage > 0 && it.maxDurability > 0 }

        if (damagedItems.isEmpty()) {
            player.sendMessage("§cTamir edilecek eşya bulunamadı.")
            Utils.stopRepairing(player.name)
            return
        }

        val text = StringBuilder()
        for (item in damagedItems) {
            val damageText = "${item.damage}/${item.maxDurability}"
            text.append("§6${item.name}: §c$damageText Hasar\n")
        }

        val form = SimpleForm("Envanter Tamiri")
        form.addElement(ElementLabel("§6Toplam §e${damagedItems.size} §6eşya listelendi:\n\n$text\n\n§aTümünü tamir etmek istiyor musunuz?"))
        form.addElement(ElementButton("§2Evet", ButtonImage(ButtonImage.Type.PATH, "textures/ui/confirm.png")))
        form.addElement(ElementButton("§cHayır", ButtonImage(ButtonImage.Type.PATH, "textures/ui/cancel.png")))

        form.send(player)

        form.onClose {
            Utils.stopRepairing(player.name)
        }

        form.onSubmit { p, response ->
            if (p == null || response == null) return@onSubmit
            if (response.buttonId() == 0) {
                handleRepair(p)
            } else {
                Utils.stopRepairing(p.name)
            }
        }
    }

    private fun handleRepair(player: Player) {
        val inventory = player.inventory
        var repairedAny = false

        for ((slot, item) in inventory.contents.entries) {
            if (item.damage > 0 && item.maxDurability > 0) {
                item.damage = 0
                inventory.setItem(slot, item)
                repairedAny = true
            }
        }

        if (repairedAny) {
            player.sendMessage("§aTüm eşyaların başarıyla tamir edildi!")
            Utils.sound(player, "note.harp")
            Utils.addRepairList(player.name)
        } else {
            player.sendMessage("§cTamir edilecek eşya bulunamadı.")
            player.level.addSound(player.position, Sound.MOB_BLAZE_SHOOT, 1f, 1f, player)
        }

        Utils.stopRepairing(player.name)
    }
}