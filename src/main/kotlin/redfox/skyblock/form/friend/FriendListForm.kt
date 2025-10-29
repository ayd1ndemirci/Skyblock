package redfox.skyblock.form.friend

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.simple.ButtonImage
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.data.Database
import redfox.skyblock.utils.Utils

object FriendListForm {

    fun send(player: Player) {
        val friendList = Database.getFriends(player.name)
        val count = friendList.size
        val form = SimpleForm("Arkadaşların")
        form.addElement(ElementLabel("§f§o$count §r§7adet arkadaşın var!"))

        for (friendName in friendList) {
            val isOnline = Server.getInstance().getPlayerExact(friendName) != null
            val icon = if (isOnline) "textures/ui/heart_new" else "textures/ui/heart_background"
            form.addElement(ElementButton(friendName, ButtonImage(ButtonImage.Type.PATH, icon)))
        }
        form.send(player)

        form.onSubmit { _, response ->
            if (response == null) return@onSubmit
            val textButton = response.button().text()
            SelectFriendForm.send(player, textButton)
            Utils.sound(player, "note.harp")
        }
    }
}
