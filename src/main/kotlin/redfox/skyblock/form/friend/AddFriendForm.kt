package redfox.skyblock.form.friend

import cn.nukkit.Player
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.custom.ElementDropdown
import cn.nukkit.form.response.CustomResponse
import cn.nukkit.form.window.CustomForm
import redfox.skyblock.Core
import redfox.skyblock.data.Database

object AddFriendForm {

    fun send(player: Player) {
        val onlinePlayers = Core.instance.server.onlinePlayers.values
            //  .filter { it.name != player.name }
            .map { it.name }
            .toMutableList()

        /* if (onlinePlayers.isEmpty()) {
             player.sendMessage("§cSunucuda senden başka oyuncu yok!")
             return
         }*/

        val form = CustomForm("Arkadaş Ekleme Menüsü")

        form.addElement(ElementDropdown("Oyuncu Seç", onlinePlayers))
        form.addElement(ElementLabel("§7Seçtiğin oyuncuya arkadaşlık isteği gönderebilirsin!"))

        form.send(player)

        form.onSubmit { _: Player?, response: CustomResponse? ->
            if (response == null) return@onSubmit

            val selectedIndex = response.getDropdownResponse(0).elementId()
            val selectedPlayer = onlinePlayers.getOrNull(selectedIndex) ?: return@onSubmit

            if (selectedPlayer == "§c§lSeçiniz") {
                player.sendMessage("§cLütfen geçerli bir oyuncu seç!")
                return@onSubmit
            }
            /*  if (selectedPlayer == player.name) {
                  player.sendMessage("§cKendini arkadaş olarak ekleyemezsin!")
                  return@onSubmit
              }*/

            val friends = Database.getFriends(player.name)
            if (friends.contains(selectedPlayer)) {
                player.sendMessage("§cBu oyuncu zaten arkadaşın!")
                return@onSubmit
            }

            val incomingRequests = Database.getIncomingRequests(player.name)
            val outgoingRequests = Database.getOutgoingRequests(player.name)

            if (outgoingRequests.contains(selectedPlayer)) {
                player.sendMessage("§cBu oyuncuya zaten arkadaşlık isteği göndermişsin!")
                return@onSubmit
            }
            if (incomingRequests.contains(selectedPlayer)) {
                player.sendMessage("§cBu oyuncu zaten sana arkadaşlık isteği göndermiş!")
                return@onSubmit
            }

            Database.sendFriendRequest(player.name, selectedPlayer)

            player.sendMessage("§a${selectedPlayer} adlı oyuncuya arkadaşlık isteği gönderildi!")
            player.server.getPlayerExact(selectedPlayer)
                ?.sendMessage("§a${player.name} adlı oyuncu sana arkadaşlık isteği gönderdi!")
        }
    }
}
