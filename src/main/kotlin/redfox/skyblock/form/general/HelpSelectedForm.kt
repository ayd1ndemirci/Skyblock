package redfox.skyblock.form.general

import cn.nukkit.Player
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.simple.ButtonImage
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.response.SimpleResponse
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.manager.HelpManager
import redfox.skyblock.utils.Utils

object HelpSelectedForm {

    fun send(player: Player, title: String) {
        val content = HelpManager.getHelp(title)

        val form = SimpleForm("Yardım: $title")

        form.addElement(ElementLabel("$content\n\n"))
        form.addElement(ElementButton("Geri", ButtonImage(ButtonImage.Type.PATH, "textures/ui/arrow_left.png")))

        form.send(player)

        form.onSubmit { _: Player, response: SimpleResponse? ->
            if (response == null) return@onSubmit

            if (response.buttonId() == 0) {
                HelpForm.send(player)
                Utils.sound(player, "note.harp")
            }
        }
    }
}
