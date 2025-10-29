package redfox.skyblock.form.island.block

import cn.nukkit.Player
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.response.SimpleResponse
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.Island
import redfox.skyblock.data.IslandDB
import redfox.skyblock.form.island.IslandForm
import redfox.skyblock.utils.IslandUtils

object BlockPlayersToIslandForm {

    fun send(player: Player, partner: Boolean) {
        var playerName = player.name
        if (partner) {
            playerName = IslandDB.getPlayerPartner(playerName) ?: playerName
        }

        val buttons = if (partner) {
            listOf(
                "§cGeri",
                "Engellenenler Listesi"
            )
        } else {
            listOf(
                "§cGeri",
                "Oyuncu Engelle",
                "Oyuncu Engelini Kaldır",
                "Engellenenler Listesi"
            )
        }

        val form = SimpleForm("Adadan Oyuncu Engelle")

        buttons.forEach { buttonText ->
            form.addElement(ElementButton(buttonText))
        }

        form.onSubmit { _: Player?, response: SimpleResponse? ->
            if (response == null) return@onSubmit

            val selectedText = buttons.getOrNull(response.buttonId()) ?: return@onSubmit

            when {
                response.buttonId() == 0 -> {
                    IslandForm.send(player, partner)
                }

                selectedText == "Oyuncu Engelle" -> {
                    val list = player.server.onlinePlayers.values
                        .filter { it.name != player.name }
                        .filter { !player.server.isOp(it.name) }
                        .filter { !player.hasPermission(IslandUtils.ISLAND_ADMIN_PERMISSION) }
                        .filter { !player.hasPermission(IslandUtils.PLAYER_INVISIBLE) }
                        .filter { !Island.isPartner(it.name, player.name) }
                        .map { it.name }
                        .toList()

                    if (list.isEmpty()) {
                        player.sendMessage("§cSunucuda adandan engelleyebileceğin kimse yok!")
                        return@onSubmit
                    }
                    BlockPlayerForm.send(player)
                }

                selectedText == "Oyuncu Engelini Kaldır" -> {
                    val blockeds =
                        IslandDB.getIsland(playerName)?.getList("blockeds", String::class.java) ?: emptyList()
                    if (blockeds.isEmpty()) {
                        player.sendMessage("§cHiç bir oyuncu adadan engellenmemiş!")
                        return@onSubmit
                    }
                    UnBlockPlayerForm.send(player, blockeds)
                }

                selectedText == "Engellenenler Listesi" -> {
                    val blockeds =
                        IslandDB.getIsland(playerName)?.getList("blockeds", String::class.java) ?: emptyList()
                    if (blockeds.isNotEmpty()) {
                        val text = blockeds.joinToString(separator = "§4, §c", prefix = "§c", postfix = "§c")
                        player.sendMessage("§6Toplamda §g${blockeds.size} §6oyuncu adadan engellenmiş: $text")
                    } else player.sendMessage("§cHiç bir oyuncu adadan engellenmemiş!")
                }
            }
        }

        form.send(player)
    }
}
