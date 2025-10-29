package redfox.skyblock.form.general

import cn.nukkit.Player
import cn.nukkit.form.element.simple.ButtonImage
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.response.SimpleResponse
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.utils.Utils

object EarnForm {

    fun send(player: Player, page: String = "main") {
        val form = SimpleForm("Para Kazanma Rehberi")

        when (page) {
            "main" -> {
                form.addElement(
                    ElementButton(
                        "Kaktüs Farmı",
                        ButtonImage(ButtonImage.Type.PATH, "textures/blocks/cactus_side.png")
                    )
                )
                form.addElement(
                    ElementButton(
                        "Karpuz Farmı",
                        ButtonImage(ButtonImage.Type.PATH, "textures/blocks/melon_side.png")
                    )
                )
                form.addElement(
                    ElementButton(
                        "Geri",
                        ButtonImage(ButtonImage.Type.PATH, "textures/ui/cancel.png")
                    )
                )
            }

            "cactus" -> {
                form.title("§aKaktüs Farmı")
                form.content("Kaktüs farmı ile otomatik toplayıp satabilirsiniz.\n\n- Otomatik kırma sistemi kurun\n- Chest'e hopper ile bağlayın\n- Marketten satışı yapın")
                form.addElement(
                    ElementButton(
                        "§cGeri",
                        ButtonImage(ButtonImage.Type.PATH, "textures/ui/cancel.png")
                    )
                )
            }

            "melon" -> {
                form.title("§2Karpuz Farmı")
                form.content("Karpuz farmı hızlı ve verimli para kazandırır.\n\n- Silk Touch balta ile toplama\n- Otomatik sistemler kurma\n- Marketten yüksek fiyatla satış")
                form.addElement(
                    ElementButton(
                        "§cGeri",
                        ButtonImage(ButtonImage.Type.PATH, "textures/ui/cancel.png")
                    )
                )
            }
        }

        form.send(player)

        form.onSubmit { _: Player, response: SimpleResponse? ->
            if (response == null) return@onSubmit
            val index = response.buttonId()
            when (page) {
                "main" -> {
                    when (index) {
                        0 -> send(player, "cactus")
                        1 -> send(player, "melon")
                        2 -> Utils.sound(player, "random.toast")
                    }
                }

                "cactus", "melon" -> {
                    if (index == 0) send(player, "main")
                }
            }
        }
    }
}
