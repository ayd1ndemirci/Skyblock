package redfox.skyblock.form.auction

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.custom.ElementInput
import cn.nukkit.form.element.custom.ElementSlider
import cn.nukkit.form.window.CustomForm
import cn.nukkit.item.Item
import redfox.skyblock.utils.Utils

object AuctionCreateForm {

    const val maxPrice = 1000000

    fun send(player: Player, item: Item, slot: Int, sliderMessage: String = "", inputMessage: String = "", instantPriceMessage: String = "") {
        val form = CustomForm("İhale Oluştur")
        form.addElement(ElementLabel(" "))
        form.addElement(ElementLabel("§uSeçilen Eşya: §f${item.count}x ${item.name}\n"))
        form.addElement(ElementSlider(if (sliderMessage == "") "Miktar" else sliderMessage, 1f, item.count.toFloat(), 1, 1f))
        form.addElement(ElementLabel(" "))
        form.addElement(ElementInput(if (inputMessage == "") "Fiyat" else inputMessage, "Örn.: 600"))
        form.addElement(ElementLabel(" "))
        form.addElement(ElementInput(if (instantPriceMessage == "") "Anında Satış Fiyatı" else instantPriceMessage, "Satış fiyatından üst bir değer gir"))
        form.send(player)

        form.onSubmit { _, response ->
            if (response == null) return@onSubmit

            val selectedCount = response.getSliderResponse(2).toInt()

            if (selectedCount <= 0 || selectedCount > 64 ||selectedCount > item.count) {
                send(player, item, slot,"§cDüzgün miktar seç, sikmim")
                return@onSubmit
            }

            val priceStr = response.getInputResponse(4)
            val instantPriceStr = response.getInputResponse(6)

            val price = priceStr.toIntOrNull()
            val instantPrice = instantPriceStr.toIntOrNull()

            if (price == null || price < 500 || price > 1000000) {
                send(player, item, slot, "", "§cFiyat 500 ile 1.000.000 TL arası olabilir.")
                return@onSubmit
            }

            if (instantPrice == null || instantPrice > 1000000) {
                send(player, item, slot, "", "", "§cAnında satış fiyatı 500 ile 1.000.000 TL arası olabilir.")
                return@onSubmit
            }

            if (instantPrice <= price) {
                send(player, item, slot, "", "", "§cAnında satış fiyatı, satış fiyatından az olamaz.")
                return@onSubmit
            }

            val handItem = player.inventory.itemInHand
            val handSlot = player.inventory.heldItemIndex
            if (
                handItem.id != item.id ||
                slot != handSlot ||
                handItem.name != item.name ||
                handItem.count != item.count ||
                handItem.customName != item.customName ||
                (handItem.namedTag == null && item.namedTag != null) ||
                (handItem.namedTag != null && item.namedTag == null) ||
                (handItem.namedTag != null && item.namedTag != null && handItem.namedTag != item.namedTag) ||
                handItem.enchantments.size != item.enchantments.size ||
                !handItem.enchantments.all { handEnch ->
                    item.enchantments.any { itemEnch ->
                        itemEnch.id == handEnch.id && itemEnch.level == handEnch.level
                    }
                }
            ) {
                ItemSelectForm.send(player, "§8» §cEşyayı değiştirme len!")
                Utils.sound(player, "note.harp")
                return@onSubmit
            }

            Utils.sound(player, "note.harp")

            Server.getInstance().broadcastMessage("\n§8-------------------------\n\n§b${player.name} §aisimli oyuncu §b${price} TL'ye ${item.count}x ${item.name} §aöğesini ihaleye koydu. Satın almak için §e/ihale\n\n§8-------------------------\n")
        }
    }
}