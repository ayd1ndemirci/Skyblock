package redfox.skyblock.form.credi

import cn.nukkit.Player
import cn.nukkit.form.element.simple.ButtonImage
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.response.SimpleResponse
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.utils.Utils

object CrediForm {
    fun send(player: Player) {

        val form = SimpleForm("Kredi Menüsü")
        form.addElement(ElementButton("Kredine Bak", ButtonImage(ButtonImage.Type.PATH, "textures/ui/MCoin.png")))
        form.addElement(ElementButton("Kredi Gönder", ButtonImage(ButtonImage.Type.PATH, "textures/ui/trade_icon.png")))
        form.addElement(ElementButton("Kredi Market", ButtonImage(ButtonImage.Type.PATH, "textures/ui/icon_best3.png")))
        form.addElement(
            ElementButton(
                "Kredi Geçmişi",
                ButtonImage(ButtonImage.Type.PATH, "textures/items/map_filled.png")
            )
        )

        form.send(player)

        form.onSubmit { _: Player?, response: SimpleResponse? ->
            if (response == null) return@onSubmit
            Utils.sound(player, "note.harp")
            when (response.buttonId()) {
                0 -> SeeCrediForm.send(player)
                1 -> SendCrediForm.send(player)
                2 -> CrediShopForm.send(player)
                3 -> CrediHistoryForm.send(player)
            }
        }
    }
}