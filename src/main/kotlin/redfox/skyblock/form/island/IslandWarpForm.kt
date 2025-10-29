package redfox.skyblock.form.island

import cn.nukkit.Player
import cn.nukkit.form.element.simple.ButtonImage
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.Island
import redfox.skyblock.data.IslandDB

object IslandWarpForm {

    fun send(player: Player, partner: Boolean = false) {
        val playerName = if (partner) IslandDB.getPlayerPartner(player.name) else player.name
        val warps = IslandDB.getWarps(playerName)
        val maxWarps = Island.getMaxWarpLimit(player)

        val form = SimpleForm("Ada Warp Menüsü")
        form.addElement(
            ElementButton(
                "Warp Noktası Oluştur §r§o(${warps.size}/$maxWarps)",
                ButtonImage(ButtonImage.Type.PATH, "textures/items/book_writable.png")
            )
        )

        warps.forEach { warpName ->
            form.addElement(ElementButton(warpName, ButtonImage(ButtonImage.Type.PATH, "textures/items/sign.png")))
        }

        form.onSubmit { _, response ->
            val id = response.buttonId()
            if (id == 0) {
                if (warps.size >= maxWarps) {
                    player.sendMessage("§cWarp limiti dolu! ($maxWarps)")
                    return@onSubmit
                }
                IslandCreateWarpForm.send(player)
            } else {
                val selectedWarp = warps.getOrNull(id - 1)
                if (selectedWarp != null) {
                    IslandWarpManageForm.send(player, partner, selectedWarp)
                }
            }
        }

        form.send(player)
    }
}
