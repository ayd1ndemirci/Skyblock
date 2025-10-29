package redfox.skyblock.form.general

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.form.element.simple.ButtonImage
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.response.SimpleResponse
import cn.nukkit.form.window.SimpleForm

object ListForm {
    fun send(player: Player) {
        val form = SimpleForm("Çevrimiçi Oyuncular")

        val onlinePlayers = Server.getInstance().onlinePlayers.values.toList()

        for (online in onlinePlayers) {
            form.addElement(
                ElementButton(
                    online.name,
                    ButtonImage(ButtonImage.Type.URL, "http://179.61.147.21:3001/head/${online.name}")
                )
            )
        }

        form.send(player)

        form.onSubmit { _: Player?, response: SimpleResponse? ->
            if (response == null) return@onSubmit

            val index = response.buttonId()
            if (index in onlinePlayers.indices) {
                val selectedPlayerName = onlinePlayers[index].name
                ProfileForm.send(player, selectedPlayerName, back = { send(player) })
            }
        }
    }
}
