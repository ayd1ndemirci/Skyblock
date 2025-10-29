package redfox.skyblock.form.island

import cn.nukkit.Player
import cn.nukkit.form.element.simple.ButtonImage
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.data.IslandDB

object IslandWarpManageForm {

    fun send(player: Player, partner: Boolean, selectedWarp: String) {
        val playerName = if (partner) IslandDB.getPlayerPartner(player.name) ?: player.name else player.name

        val form = SimpleForm("Ada Warpı - $selectedWarp")

        form.addElement(ElementButton("Ada Warpına Işınlan"))
        form.addElement(ElementButton("Ada Warpını Sil"))
        form.addElement(ElementButton("Geri", ButtonImage(ButtonImage.Type.PATH, "textures/ui/arrow_left.png")))

        form.onSubmit { _, response ->
            if (response == null) return@onSubmit
            when (response.buttonId()) {
                0 -> {
                    val position = IslandDB.getWarpPosition(selectedWarp, playerName)
                    if (position == null) {
                        player.sendMessage("§cAda noktasına ışınlanırken bir sorun oluştu!")
                        return@onSubmit
                    }
                    player.teleport(position)
                    player.sendMessage("§g$selectedWarp §6adlı warp noktasına ışınlandın.")
                }

                1 -> {
                    val warpsDoc = IslandDB.getIsland(playerName)?.get("warps", org.bson.Document::class.java)
                    val warps =
                        warpsDoc?.entries?.associate { it.key to it.value.toString() }?.toMutableMap() ?: mutableMapOf()

                    warps.remove(selectedWarp)
                    val newWarpsDoc = org.bson.Document()
                    warps.forEach { (k, v) -> newWarpsDoc.append(k, v) }
                    IslandDB.collection.updateOne(
                        com.mongodb.client.model.Filters.eq("player", playerName),
                        com.mongodb.client.model.Updates.set("warps", newWarpsDoc)
                    )
                    player.sendMessage("§g$selectedWarp §6adlı ada warpı silindi!")
                }

                2 -> IslandWarpForm.send(player, partner)
            }
        }

        form.send(player)
    }
}
