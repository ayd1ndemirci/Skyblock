package redfox.skyblock.form.island

import cn.nukkit.Player
import cn.nukkit.form.element.simple.ButtonImage
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.response.SimpleResponse
import cn.nukkit.form.window.SimpleForm

object IslandVisitForm {

    fun send(player: Player, partner: Boolean = false) {
        val form = SimpleForm("Ada Ziyaret Menüsü")

        form.addElement(
            ElementButton(
                "Ada Ziyaret Ayarların",
                ButtonImage(ButtonImage.Type.PATH, "textures/ui/gear.png")
            )
        )
        form.addElement(
            ElementButton(
                "Ziyarete Açık Olan Adalar",
                ButtonImage(ButtonImage.Type.PATH, "textures/ui/worldsIcon.png")
            )
        )

        form.onSubmit { _: Player?, response: SimpleResponse? ->
            if (response == null) return@onSubmit
            when (response.buttonId()) {
                0 -> player.sendForm(IslandVisitSettingForm(player, partner))
                1 -> player.sendForm(IslandVisitPlayersForm(player, partner))
            }
        }

        form.send(player)
    }
}
