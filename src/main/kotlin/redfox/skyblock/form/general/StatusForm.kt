package redfox.skyblock.form.general

import cn.nukkit.Player
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.custom.ElementInput
import cn.nukkit.form.element.custom.ElementToggle
import cn.nukkit.form.window.CustomForm
import redfox.skyblock.group.GroupManager
import redfox.skyblock.utils.Utils

object StatusForm {
    fun send(player: Player, message: String = "") {
        val key = player.name.lowercase()
        val currentStatus = Utils.statusPlayers[key] ?: "Durum girilmemiş."

        val form = CustomForm("Durum Ayarları")
        form.addElement(ElementLabel("§bMevcut Durumun: §3$currentStatus"))
        form.addElement(ElementToggle("Durumumu silmek istiyorum", false))
        form.addElement(ElementLabel(message))
        form.addElement(
            ElementInput(
                "Yeni durum gir (32 karakter max):",
                "Abilere selam çatışmaya devamq",
                currentStatus.takeIf { it != "Durum girilmemiş." } ?: ""
            )
        )

        form.send(player)

        form.onSubmit { _, response ->
            val deleteStatus = response!!.getToggleResponse(1)
            val input = response.getInputResponse(3)

            if (deleteStatus) {
                if (Utils.statusPlayers.containsKey(key)) {
                    Utils.statusPlayers.remove(key)
                    val format = GroupManager.getPlayerGroup(player).nameTagFormat
                    val replacedFormat = format.replace("%nickname%", player.name)
                    player.nameTag = replacedFormat
                    player.sendMessage("§8» §aDurumun silindi.")
                    Utils.sound(player, "note.harp")
                } else {
                    player.sendMessage("§8» §cZaten bir durumun yok.")
                    Utils.sound(player, "item.trident.hit_ground")
                }
                return@onSubmit
            }

            if (input.isNullOrBlank()) {
                send(player, "§cBir durum belirtmelisin!")
                Utils.sound(player, "item.trident.hit_ground")
                return@onSubmit
            }

            if ('§' in input) {
                send(player, "§cLütfen renk kodu kullanmayınız!")
                Utils.sound(player, "item.trident.hit_ground")
                return@onSubmit
            }

            if (input.length > 32) {
                send(player, "§cDurum mesajınız en fazla 32 karakter olabilir!")
                Utils.sound(player, "item.trident.hit_ground")
                return@onSubmit
            }

            Utils.statusPlayers[key] = input
            val format = GroupManager.getPlayerGroup(player).nameTagFormat
            val replacedFormat = format.replace("%nickname%", player.name)
            player.nameTag = "$replacedFormat\n§a§o$input"
            player.sendMessage("§8» §aDurumun §2'$input' §aolarak güncellendi.")
        }
    }
}
