package redfox.skyblock.form.general

import cn.nukkit.Player
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.window.SimpleForm

object VIPInfoForm {

    fun send(player: Player) {
        val form = SimpleForm("VIP Bilgi")
        form.addElement(ElementButton("VIP"))
        form.addElement(ElementButton("VIP+"))
        form.addElement(ElementButton("MVIP"))

        form.send(player)

        form.onSubmit { _: Player, response ->
            if (response == null) return@onSubmit

            when (response.buttonId()) {
                0 -> player.sendMessage("test")
            }
        }
    }
}