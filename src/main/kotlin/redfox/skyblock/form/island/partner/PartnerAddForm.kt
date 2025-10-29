package redfox.skyblock.form.island.partner

import cn.nukkit.Player
import cn.nukkit.form.element.custom.ElementDropdown
import cn.nukkit.form.response.CustomResponse
import cn.nukkit.form.window.CustomForm
import redfox.skyblock.Island
import redfox.skyblock.data.IslandDB
import redfox.skyblock.utils.IslandUtils

object PartnerAddForm {

    fun send(player: Player) {
        val onlineNames = player.server.onlinePlayers.values
            .filter { it.name != player.name }
            .map { it.name }

        if (onlineNames.isEmpty()) {
            player.sendMessage("§l§6Ada §r§cÇevrim içi başka oyuncu yok!")
            return
        }

        val form = CustomForm("Ortak Ekle")
        form.addElement(ElementDropdown("Oyuncu Seç:", onlineNames))

        form.send(player)

        form.onSubmit { _: Player?, response: CustomResponse? ->
            if (response == null) return@onSubmit
            val selectedName = onlineNames[response.getDropdownResponse(0).elementId()]

            val selectedPlayer = player.server.getPlayer(selectedName)
            if (selectedPlayer == null || !selectedPlayer.isOnline) {
                player.sendMessage("§cBu oyuncu oyundan çıkış yapmış!")
                return@onSubmit
            }

            if (IslandDB.getIsland(selectedName) == null) {
                if (Island.isPartner(selectedName, player.name)) {
                    player.sendMessage("§cBu oyuncuyla zaten ortaksın!")
                    return@onSubmit
                }

                val invitations = IslandUtils.partnershipInvitations[selectedName] ?: listOf()
                if (player.name in invitations) {
                    player.sendMessage("§cBu oyuncuya zaten ortaklık isteği göndermişsiniz!")
                    return@onSubmit
                }

                if (IslandDB.getPlayerPartner(selectedName) != null) {
                    player.sendMessage("§cBu oyuncu başka bir adaya ortak olmuş!")
                    return@onSubmit
                }

                selectedPlayer.sendMessage("§g${player.name} §6isimli oyuncu size ortaklık isteği gönderdi. Kabul etmek için: §g'/ada'")
                IslandUtils.addPartnershipInvitation(selectedName, player.name)
                player.sendMessage("§g$selectedName §6adlı oyuncuya ortaklık isteği gönderildi!")
            } else {
                player.sendMessage("§cBu oyuncunun zaten bir adası var! Ortak eklemek için adası olmaması gerekir.")
            }
        }
    }
}
