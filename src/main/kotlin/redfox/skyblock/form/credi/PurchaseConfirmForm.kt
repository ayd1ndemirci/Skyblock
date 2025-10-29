package redfox.skyblock.form.credi

import cn.nukkit.Player
import cn.nukkit.form.window.ModalForm
import cn.nukkit.level.Sound
import redfox.skyblock.data.Database
import redfox.skyblock.manager.ShopItem
import redfox.skyblock.model.HistoryRecord
import redfox.skyblock.utils.Utils

object PurchaseConfirmForm {

    fun send(player: Player, item: ShopItem) {
        val form = ModalForm("Onay Menüsü")
            .content(
                "§e${
                    item.name.replace(
                        "\n",
                        " "
                    )
                } adlı ürünü §6${item.cost} kredi§f karşılığında satın almak istiyor musun?"
            )
            .text("§aSatın Al", "§c<- Geri")
            .yes("§aSatın Al") { p ->
                if (!Database.hasCredi(p.name, item.cost)) {
                    p.sendMessage("§cYeterli kredin yok! Gerekli kredi: ${item.cost}")
                    Utils.sound(p, "item.trident.hit_ground")
                    return@yes
                }

                Database.removeCredi(p.name, item.cost)
                item.action(p)

                Database.addCrediRecord(
                    p.name,
                    HistoryRecord(
                        name = p.name,
                        action = "Satın Alım: ${item.name.replace("\n", " ")}",
                        amount = item.cost
                    )
                )


                p.sendMessage("§a${item.name.replace("\n", " ")} başarıyla satın alındı!")
                p.level.addSound(p.position, Sound.RANDOM_ANVIL_USE, 1f, 1f, p)
            }
            .no("§c<- Geri") { p ->
                CrediShopForm.send(p)
                Utils.sound(player, "note.harp")
            }

        form.send(player)
    }
}