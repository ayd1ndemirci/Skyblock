package redfox.skyblock.form.island

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.custom.ElementToggle
import cn.nukkit.form.response.CustomResponse
import cn.nukkit.form.window.CustomForm
import redfox.skyblock.Island
import redfox.skyblock.data.IslandDB
import redfox.skyblock.utils.IslandUtils

object IslandKickPlayersForm {

    fun send(player: Player, partner: Boolean = false) {
        val playerName = if (partner) IslandDB.getPlayerPartner(player.name) ?: player.name else player.name
        val world = Server.getInstance().getLevelByName(playerName)

        if (world == null || !Server.getInstance().isLevelLoaded(playerName)) {
            player.sendMessage("§cDünya yüklenemedi!")
            return
        }

        val kickablePlayers = mutableListOf<String>()

        for (p in world.players.values) {
            if (p.name == playerName ||
                Island.isPartner(p.name, playerName) ||
                p.hasPermission(IslandUtils.PLAYER_INVISIBLE) ||
                p.hasPermission(IslandUtils.ISLAND_ADMIN_PERMISSION)
            ) continue
            kickablePlayers.add(p.name)
        }

        if (kickablePlayers.isEmpty()) {
            val errorForm = CustomForm("Oyuncu Tekmeleme Menüsü - Hata")
            errorForm.addElement(ElementLabel("§4§lHATA: §r§cTekmelenebilecek kimse yok!"))
            errorForm.send(player)
            return
        }

        val form = CustomForm("Oyuncu Tekmeleme Menüsü")
        form.addElement(ElementLabel("§eTekmelemek istediğin oyuncuları aşağıdan seç:"))

        for (name in kickablePlayers) {
            form.addElement(ElementToggle("§c$name", false))
        }

        form.send(player)

        form.onSubmit { _, response: CustomResponse? ->
            if (response == null) return@onSubmit

            val kicked = mutableListOf<String>()

            for ((index, name) in kickablePlayers.withIndex()) {
                if (response.getToggleResponse(index + 1)) {
                    val target = Server.getInstance().getPlayerExact(name)
                    if (target != null) {
                        target.teleport(Server.getInstance().defaultLevel.safeSpawn)
                        target.sendMessage("§4§o${player.name} §r§cisimli oyuncu sizi adasından tekmeledi.")
                        kicked.add(name)
                    }
                }
            }

            if (kicked.isNotEmpty()) {
                player.sendMessage("§8» §aSeçtiğiniz oyuncular tekmelendi: §2§o${kicked.joinToString(", ")}")
            }
        }
    }
}
