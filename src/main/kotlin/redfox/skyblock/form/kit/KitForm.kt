package redfox.skyblock.form.kit

import cn.nukkit.Player
import cn.nukkit.form.element.simple.ButtonImage
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.response.SimpleResponse
import cn.nukkit.form.window.SimpleForm

object KitForm {

    private val kits = listOf(
        Triple("VIP", ButtonImage.Type.URL, "https://hypixel.net/attachments/676982/"),
        Triple("VIP+", ButtonImage.Type.URL, "https://i.imgur.com/qN06RPM.png"),
        Triple("MVIP", ButtonImage.Type.PATH, "https://i.imgur.com/xEXgFPP.png")
    )

    fun send(player: Player) {
        val form = SimpleForm("Kit Menüsü")

        kits.forEach { (name, imageType, imagePath) ->
            val image = ButtonImage(imageType, imagePath)
            val button = ElementButton(name, image)
            form.addElement(button)
        }

        form.send(player)

        form.onSubmit { _: Player?, response: SimpleResponse? ->
            if (response == null) return@onSubmit
            val index = response.buttonId()
            if (index in kits.indices) {
                KitInfoForm.send(player, kits[index].first)
            }
        }
    }
}
