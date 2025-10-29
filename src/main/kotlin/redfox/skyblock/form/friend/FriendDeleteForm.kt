package redfox.skyblock.form.friend

import cn.nukkit.Player
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.data.Database
import redfox.skyblock.utils.Utils

object FriendDeleteForm {

    fun send(player: Player, friendName: String) {
        val form = SimpleForm("Arkadaş Silme - ${friendName}")
        form.addElement(ElementLabel("${friendName} adlı arkadaşını arkadaşlık listenden silmeyi onaylıyor musun?\n\n"))
        form.addElement(ElementButton("Evet"))
        form.addElement(ElementButton("Hayır"))
        form.send(player)

        form.onSubmit { _, response ->
            if (response == null) return@onSubmit

            when (response.buttonId()) {
                0 -> {
                    Database.removeFriend(player.name, friendName)
                    player.sendMessage("$friendName adlı oyuncuyu arkadaşlıktan çıkardın.")
                    Utils.sound(player, "note.harp")


                    val friend = player.server.getPlayerExact(friendName)
                    friend?.sendMessage("${player.name} adlı oyuncuyu seni arkadaşlıktan çıkardı.")
                    Utils.sound(friend, "item.trident.hit_ground")
                }
            }
        }
    }
}