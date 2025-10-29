package redfox.skyblock.form.friend

import cn.nukkit.Player
import cn.nukkit.form.element.simple.ButtonImage
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.data.Database
import redfox.skyblock.utils.Utils

object FriendForm {

    fun send(player: Player) {
        val form = SimpleForm("Arkadaş Menüsü")
        form.addElement(
            ElementButton(
                "Arkadaşlarım",
                ButtonImage(ButtonImage.Type.PATH, "textures/ui/FriendsDiversity")
            )
        )
        form.addElement(
            ElementButton(
                "Arkadaş Ekle",
                ButtonImage(ButtonImage.Type.PATH, "textures/ui/dressing_room_customization")
            )
        )
        form.addElement(ElementButton("İstek Kutusu", ButtonImage(ButtonImage.Type.PATH, "textures/ui/invite_base")))
        form.send(player)

        form.onSubmit { _: Player, response ->
            if (response == null) return@onSubmit

            when (response.buttonId()) {
                0 -> {
                    FriendListForm.send(player)
                    Utils.sound(player, "note.harp")
                }

                1 -> {
                    /* if (player.server.onlinePlayers.size <= 1) {
                         player.sendMessage("§cSunucuda senden başka kimse yok.")
                         return@onSubmit
                     }*/
                    AddFriendForm.send(player)
                    Utils.sound(player, "note.harp")
                }

                2 -> {
                    val incomingRequests = Database.getIncomingRequests(player.name)

                    if (incomingRequests.isEmpty()) {
                        player.sendMessage("§cHiç arkadaşlık isteğin yok :(")
                        return@onSubmit
                    }
                    FriendRequestForm.send(player)
                    Utils.sound(player, "note.harp")
                }
            }
        }
    }
}