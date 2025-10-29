package redfox.skyblock.form.island.block

import cn.nukkit.Player
import cn.nukkit.form.element.custom.ElementDropdown
import cn.nukkit.form.response.CustomResponse
import cn.nukkit.form.window.CustomForm
import redfox.skyblock.Island
import redfox.skyblock.data.IslandDB

object BlockPlayerForm {

    fun send(player: Player) {
        val islandData = IslandDB.getIsland(player.name)
        val blockeds = islandData?.getList("blockeds", String::class.java)?.toSet() ?: emptySet()

        val list = player.server.onlinePlayers.values
            .filter { it.name != player.name }
            .filter { !player.server.isOp(it.name) }
            .filter { !player.hasPermission("island.admin") }
            .filter { !player.hasPermission("player.invisible") }
            .filter { !Island.isPartner(it.name, player.name) }
            .filter { it.name !in blockeds }  // Engellenmiş olanları çıkar
            .map { it.name }
            .toList()

        if (list.isEmpty()) {
            player.sendMessage("§cSunucuda adandan engelleyebileceğin kimse yok!")
            return
        }

        val form = CustomForm("Oyuncu Engelle")
        form.addElement(ElementDropdown("Oyuncu Seç:", list))
        form.send(player)

        form.onSubmit { _: Player?, response: CustomResponse? ->
            if (response == null) return@onSubmit
            val selectedIndex = response.getDropdownResponse(0).elementId()
            val selected = list.getOrNull(selectedIndex) ?: return@onSubmit
            val selectedPlayer = player.server.getPlayerExact(selected)

            if (selectedPlayer == null) {
                player.sendMessage("§cEngellemek istediğiniz oyuncu oyundan çıkmış!")
                return@onSubmit
            }

            val updatedBlockeds = blockeds.toMutableSet()
            if (selected in updatedBlockeds) {
                player.sendMessage("§cBu oyuncu zaten adadan engellenmiş!")
                return@onSubmit
            }

            updatedBlockeds.add(selected)
            IslandDB.updateBlockedsPlayer(player.name, updatedBlockeds.toList())

            if (selectedPlayer.level.folderName == player.name) {
                selectedPlayer.teleport(selectedPlayer.server.defaultLevel.safeSpawn)
                selectedPlayer.sendMessage("§cAdadan engellendiniz.")
            }

            player.sendMessage("§a$selected adlı oyuncu adandan engellendi.")
        }
    }
}
