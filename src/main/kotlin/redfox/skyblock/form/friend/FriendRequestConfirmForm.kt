package redfox.skyblock.form.friend

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.data.Database
import redfox.skyblock.utils.Utils

object FriendRequestConfirmForm {

    fun send(player: Player, friendName: String) {
        val form = SimpleForm("$friendName - Arkadaşlık Onayı")
        form.addElement(ElementLabel("§2$friendName §aadlı oyuncunun arkadaşlık isteğini kabul etmeyi onaylıyor musunuz?\n\n§7Bu kişiyi arkadaş eklersen istediği zaman sana ışınlanabilicek."))
        form.addElement(ElementButton("§2Evet"))
        form.addElement(ElementButton("§cHayır"))
        form.send(player)

        form.onSubmit { _, response ->
            if (response == null) return@onSubmit

            when (response.buttonId()) {
                0 -> {
                    Database.acceptFriendRequest(player.name, friendName)

                    player.sendMessage("§2§o$friendName §r§aadlı oyuncunun isteğini kabul ettin!")
                    Utils.sound(player, "note.harp")

                    val friend = Server.getInstance().getPlayerExact(friendName)
                    if (friend != null && friend.isOnline) {
                        friend.sendMessage("§2§o${player.name} §r§aarkadaşlık isteğini kabul etti!")
                        Utils.sound(friend, "note.harp")
                    }
                }

                1 -> {
                    Database.cancelFriendRequest(friendName, player.name)

                    player.sendMessage("§2§o$friendName §r§aadlı oyuncunun arkadaşlık isteğini reddettin!")
                    Utils.sound(player, "item.trident.hit_ground")

                    val friend = Server.getInstance().getPlayerExact(friendName)
                    if (friend != null && friend.isOnline) {
                        friend.sendMessage("§2§o${player.name} §r§aarkadaşlık isteğini reddetti!")
                        Utils.sound(friend, "item.trident.hit_ground")
                    }
                }
            }
        }

    }
}
