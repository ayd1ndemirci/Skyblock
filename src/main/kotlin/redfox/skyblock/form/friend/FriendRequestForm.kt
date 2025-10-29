package redfox.skyblock.form.friend

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.simple.ButtonImage
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.data.Database
import redfox.skyblock.utils.Utils

object FriendRequestForm {

    fun send(player: Player) {
        val incomingRequests = Database.getIncomingRequests(player.name)
        val form = SimpleForm("Arkadaşlık İsteklerin")
        form.addElement(ElementLabel("§f§o${incomingRequests.size} §r§7adet arkadaşlık isteğin var!"))
        if (incomingRequests.isEmpty()) {
            form.addElement(
                ElementButton(
                    "§cHiç isteğin yok!",
                    ButtonImage(ButtonImage.Type.PATH, "textures/ui/icon_warning")
                )
            )
        } else {
            for (name in incomingRequests) {
                val online = Server.getInstance().getPlayerExact(name) != null
                val iconPath = if (online) "textures/ui/heart_new" else "textures/ui/heart_background"
                form.addElement(ElementButton(name, ButtonImage(ButtonImage.Type.PATH, iconPath)))
            }
        }

        form.onSubmit { _, response ->
            val index = response.buttonId()
            if (incomingRequests.isEmpty() || index >= incomingRequests.size) return@onSubmit

            val selectedName = incomingRequests[index]
            FriendRequestConfirmForm.send(player, selectedName)
            Utils.sound(player, "note.harp")
        }

        form.send(player)
    }
}
