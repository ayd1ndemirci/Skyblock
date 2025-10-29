package redfox.skyblock.form.shop

import cn.nukkit.Player
import cn.nukkit.form.element.simple.ButtonImage
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.manager.ShopManager
import redfox.skyblock.utils.Utils

object ShopItemCategoryForm {

    fun send(player: Player, category: String) {
        val items = ShopManager.getItems(category)
        if (items.isEmpty()) {
            player.sendMessage("§cBu kategoride ürün bulunamadı.")
            return
        }

        val form = SimpleForm("$category Kategorisi", "§7Satın almak istediğin ürünü seç:")

        items.forEach {
            val label = it.name
            val id = it.id.removePrefix("minecraft:")

            form.addButton(label, ButtonImage(ButtonImage.Type.PATH, it.image))
        }

        form.send(player)

        form.onSubmit { _, response ->
            val index = response?.buttonId() ?: return@onSubmit
            val item = items.getOrNull(index) ?: return@onSubmit
            BuyForm.send(player, item)
            Utils.sound(player, "note.harp")
        }
    }
}
