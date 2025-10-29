package redfox.skyblock.form.home

import cn.nukkit.Player
import cn.nukkit.form.element.simple.ButtonImage
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.window.SimpleForm
import cn.nukkit.utils.TextFormat
import redfox.skyblock.data.HomeDB
import redfox.skyblock.utils.HomeUtil

object HomeForm {
    fun send(player: Player) {
        val homes = HomeDB.getHomes(player)
        val max = HomeUtil.getLimit(player)

        val form = SimpleForm("Ev Menüsü")
        form.addElement(
            ElementButton(
                "Ev Oluştur §r§o(${homes.size}/$max)",
                ButtonImage(ButtonImage.Type.PATH, "textures/ui/icon_recipe_nature")
            )
        )
        if (!homes.isEmpty()) {
            homes.keys.forEach { homeName ->
                form.addElement(
                    ElementButton(
                        TextFormat.clean(homeName),
                        ButtonImage(ButtonImage.Type.PATH, "textures/ui/worldsIcon.png")
                    )
                )
            }
        }

        form.onSubmit { _, response ->
            val index = response.buttonId()
            if (index == 0) {
                if (homes.size >= max) {
                    player.sendMessage("§cEv limitine ulaştın.")
                    return@onSubmit
                }
                SetHomeForm.send(player)
            } else {
                val homeName = homes.keys.elementAtOrNull(index - 1)
                if (homeName != null) {
                    ManageHomeForm.send(player, homeName)
                }
            }
        }

        form.send(player)
    }
}
