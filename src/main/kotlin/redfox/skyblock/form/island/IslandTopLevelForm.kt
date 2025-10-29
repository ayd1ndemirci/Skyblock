package redfox.skyblock.form.island

import cn.nukkit.Player
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.simple.ButtonImage
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.response.SimpleResponse
import cn.nukkit.form.window.SimpleForm
import org.bson.Document
import redfox.skyblock.data.IslandDB

object IslandTopLevelForm {

    fun send(player: Player, partner: Boolean = false) {
        val playerName = IslandDB.getPlayerPartner(player.name)?.takeIf { partner } ?: player.name

        // Tüm adaları çek ve seviye sırasına göre sırala
        val allIslands = IslandDB.getIslands()
            .mapNotNull { doc ->
                val name = doc.getString("player") ?: return@mapNotNull null
                val level = doc.getInteger("level", 0)
                name to level
            }
            .sortedByDescending { it.second }
            .take(10)

        // Form oluştur
        val form = SimpleForm("Ada Seviye Sıralaması")

        val textBuilder = StringBuilder("        §a§lADA SEVIYE SIRALAMASI§r\n\n")

        allIslands.forEachIndexed { index, (name, level) ->
            val color = when (index + 1) {
                1 -> "§l§a"
                2 -> "§l§6"
                3 -> "§l§c"
                else -> "§7"
            }

            val displayName = if (name.equals(playerName, ignoreCase = true)) {
                "$name §f(§dSEN§f)"
            } else name

            textBuilder.append("$color${index + 1}. §r§e$displayName §g=> §6Sv. $level\n")
        }

        form.addElement(ElementLabel(textBuilder.toString()))
        form.addElement(ElementButton("Geri", ButtonImage(ButtonImage.Type.PATH, "textures/ui/arrow_left.png")))

        form.onSubmit { _, response ->
            if (response?.buttonId() == 0) {
                IslandForm.send(player, partner)
            }
        }

        form.send(player)
    }
}
