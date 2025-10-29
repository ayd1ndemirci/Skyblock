package redfox.skyblock.form.general

import cn.nukkit.Player
import cn.nukkit.form.element.simple.ButtonImage
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.response.SimpleResponse
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.manager.HelpManager
import redfox.skyblock.utils.Utils

object HelpForm {

    fun send(player: Player) {
        val form = SimpleForm("Yardım Menüsü")

        val helps = HelpManager.getAllHelps()

        for (helpTitle in helps) {
            form.addElement(
                ElementButton(
                    helpTitle,
                    ButtonImage(ButtonImage.Type.PATH, "textures/blocks/bookshelf.png")
                )
            )
        }

        form.send(player)

        form.onSubmit { _: Player, response: SimpleResponse? ->
            if (response == null) return@onSubmit

            val index = response.buttonId()
            if (response.button().text() == "Para Nasıl Kazanılır") {
                EarnForm.send(player)
            }
            if (index in helps.indices) {
                val selectedHelp = helps[index]
                HelpSelectedForm.send(player, selectedHelp)
                Utils.sound(player, "note.harp")
            }
        }
    }
}
