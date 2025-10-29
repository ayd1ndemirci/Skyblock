package redfox.skyblock.form.credi

import cn.nukkit.Player
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.simple.ButtonImage
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.response.SimpleResponse
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.data.Database
import redfox.skyblock.utils.Utils

object SeeCrediForm {
    fun send(player: Player) {
        val credi = Database.getCredi(player.name)

        val form = SimpleForm("Kredi Menüsü")
        form.addElement(ElementLabel("§dKredin: §f${credi}"))
        form.addElement(ElementButton("Geri", ButtonImage(ButtonImage.Type.PATH, "textures/ui/arrow_left.png")))

        form.send(player)

        form.onSubmit { _: Player?, response: SimpleResponse? ->
            if (response == null) return@onSubmit

            when (response.buttonId()) {
                0 -> {
                    CrediForm.send(player)
                    Utils.sound(player, "note.harp")
                }
            }
        }
    }
}