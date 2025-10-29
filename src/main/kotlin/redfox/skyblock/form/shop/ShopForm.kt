package redfox.skyblock.form.shop

import cn.nukkit.Player
import cn.nukkit.form.element.simple.ButtonImage
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.manager.ShopManager
import redfox.skyblock.utils.Utils

object ShopForm {
    fun send(player: Player) {
        val categories = ShopManager.getCategories()
        val form = SimpleForm("Market")

        categories.forEach { category ->
            val image = ShopManager.getCategoryImage(category)
            form.addElement(ElementButton(category, ButtonImage(ButtonImage.Type.PATH, image)))
        }

        form.send(player)

        form.onSubmit { _, response ->
            val index = response?.buttonId() ?: return@onSubmit
            val category = categories[index]
            ShopItemCategoryForm.send(player, category)
            Utils.sound(player, "note.harp")
        }
    }
}
