package redfox.skyblock.form.island

import cn.nukkit.Player
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.simple.ButtonImage
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.response.SimpleResponse
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.Island
import redfox.skyblock.data.IslandDB

object IslandLevelForm {

    fun send(player: Player, partner: Boolean = false) {
        val playerName = if (partner) IslandDB.getPlayerPartner(player.name) ?: player.name else player.name
        val islandData = IslandDB.getIsland(playerName)
        val level = islandData?.getInteger("level") ?: 1
        val xp = islandData?.getInteger("xp") ?: 0
        val needXp = Island.getNeedXP(level)
        val remaining = Island.getRemainingXp(xp, needXp)
        val percentage = Island.getPercentageToNextLevel(xp, needXp)

        // Ada sınırları
        val maxCactus = Island.MAX_LIMITS["cactus"] ?: 0
        val maxHopper = Island.MAX_LIMITS["hopper"] ?: 0
        val currentCactus = Island.getBlockCount(playerName, "cactus")
        val currentHopper = Island.getBlockCount(playerName, "hopper")

        val form = SimpleForm("Ada Seviyesi ve Limitleri")

        val content = """
            §6Ada Seviyesi: §g$level
            §6İlerlemen: §g$xp / $needXp
            §6Kalan: §g$remaining
            §6Leveli tamamlamana kalan yüzdelik: §g$percentage%
            
            §6Ada Limitleri:
            §uKaktüs: §f$currentCactus / $maxCactus
            §uHopper: §f$currentHopper / $maxHopper
        """.trimIndent()

        form.addElement(ElementLabel(content))

        form.addElement(ElementButton("Geri", ButtonImage(ButtonImage.Type.PATH, "textures/ui/arrow_left.png")))

        form.onSubmit { _: Player?, response: SimpleResponse? ->
            if (response == null) return@onSubmit
            if (response.buttonId() == 0) {
                IslandForm.send(player, partner)
            }
        }

        form.send(player)
    }
}
