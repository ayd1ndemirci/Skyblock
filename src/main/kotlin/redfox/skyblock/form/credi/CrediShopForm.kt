package redfox.skyblock.form.credi

import cn.nukkit.Player
import cn.nukkit.form.element.simple.ButtonImage
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.manager.ShopItemManager
import redfox.skyblock.utils.Utils

object CrediShopForm {

    fun send(player: Player) {
        val form = SimpleForm("Kredi Market")

        ShopItemManager.items.forEach { item ->
            form.addElement(ElementButton("${item.name} §o${item.cost} Kredi"))
        }
        form.addElement(ElementButton("Geri", ButtonImage(ButtonImage.Type.PATH, "textures/ui/arrow_left.png")))

        form.send(player)

        form.onSubmit { _, response ->
            if (response == null) return@onSubmit

            val index = response.buttonId()

            if (index == ShopItemManager.items.size) {
                CrediForm.send(player)
                return@onSubmit
            }

            val item = ShopItemManager.items.getOrNull(index) ?: return@onSubmit

            PurchaseConfirmForm.send(player, item)
            Utils.sound(player, "note.harp")
        }
    }
}