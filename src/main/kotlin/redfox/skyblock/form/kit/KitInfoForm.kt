package redfox.skyblock.form.kit

import cn.nukkit.Player
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.simple.ButtonImage
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.data.Database
import redfox.skyblock.form.kit.manager.KitManager
import redfox.skyblock.utils.Utils

object KitInfoForm {

    fun send(player: Player, kitName: String) {
        val form = SimpleForm("$kitName Kiti")
        val itemList = KitManager.getKitItems(kitName)

        val contentText = buildString {
            append("§7İçerik:\n")
            itemList.forEach {
                append("§8- §f${it.name} x${it.count}")
                if (it.enchantments.isNotEmpty()) {
                    append(" §e(")
                    append(it.enchantments.joinToString { ench -> "${ench.name} ${ench.level}" })
                    append(")")
                }
                append("\n")
            }

            val now = System.currentTimeMillis() / 1000
            val nextTime = KitManager.getNextAvailableTime(player.name, kitName)
            val remaining = if (nextTime != null) nextTime - now else 0

            if (remaining <= 0) {
                append("\n§aBu kiti alabilirsiniz!")
            } else {
                val formatted = KitManager.formatTime(remaining)
                append("\n§cBu kiti tekrar almak için kalan süre: §f$formatted")
            }
        }

        form.addElement(ElementLabel(contentText))
        form.addElement(
            ElementButton(
                "§2Kiti Al",
                ButtonImage(ButtonImage.Type.PATH, "textures/ui/icon_blackfriday.png")
            )
        )

        form.send(player)

        form.onSubmit { _, _ ->
            if (!KitManager.canTakeKit(player.name, kitName)) {
                val nextTime = Database.getLastKitTakenTime(player.name, kitName) ?: 0L
                val now = System.currentTimeMillis() / 1000
                val remaining = nextTime + KitManager.cooldownSeconds - now

                val msg = if (remaining > 0) {
                    "§cBu kiti tekrar alabilmek için §f${KitManager.formatTime(remaining)} §cbeklemelisin."
                } else {
                    "§cBu kiti şu anda alamazsın."
                }
                Utils.sound(player, "note.harp")
                player.sendMessage(msg)
                return@onSubmit
            }

            for (item in itemList) {
                player.inventory.addItem(item)
            }

            KitManager.setKitTaken(player.name, kitName)
            player.sendMessage("§a$kitName kiti başarıyla verildi!")
            Utils.sound(player, "random.levelup")
        }
    }
}
