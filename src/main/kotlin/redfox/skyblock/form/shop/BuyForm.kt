package redfox.skyblock.form.shop

import cn.nukkit.Player
import cn.nukkit.form.element.custom.ElementInput
import cn.nukkit.form.window.CustomForm
import redfox.skyblock.data.Database
import redfox.skyblock.manager.ShopItemData
import redfox.skyblock.utils.Utils

object BuyForm {
    fun send(player: Player, item: ShopItemData) {
        val playerMoney = Database.getMoney(player)
        val maxAmount = playerMoney / item.price

        val form = CustomForm("Market Satın Alma İşlemi")
        form.addElement(
            ElementInput(
                "§6Alınacak Eşya: §g${item.name}\n\n" +
                        "§6Tane Fiyatı: §g${item.price} RF\n\n" +
                        "§6Paran: §g${playerMoney} RF\n\n" +
                        "§aBu eşyadan §2$maxAmount §aadet alabilirsin",
                "Miktar gir...".trimIndent()
            )
        )

        form.send(player)

        form.onSubmit { _, response ->
            val input = response?.getInputResponse(0) ?: return@onSubmit
            val count = input.toIntOrNull()
            if (count == null || count <= 0) {
                player.sendMessage("§cGeçersiz miktar!")
                return@onSubmit
            }

            val total = item.price * count

            if (!Database.hasMoney(player, total)) {
                player.sendMessage("§cYeterli paran yok! Gerekli: §6$total RF")
                return@onSubmit
            }
            BuyConfirmForm.send(player, item, count)
            Utils.sound(player, "note.harp")
        }
    }
}
