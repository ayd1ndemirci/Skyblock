package redfox.skyblock.form.auction

import cn.nukkit.Player
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.window.SimpleForm

object AuctionForm {

    fun send(player: Player) {
        val form = SimpleForm("İhale Menüsü")
        form.addElement(ElementButton("İhale Bilgi"))
        form.addElement(ElementButton("Mevcut İhaleleler"))
        form.addElement(ElementButton("İhale Oluştur"))
        form.addElement(ElementButton("Depo"))
        form.send(player)

        form.onSubmit { _, response ->
            if (response == null) return@onSubmit

            when (response.buttonId()) {
                0 -> AuctionInfoForm.send(player)
                1 -> ""//
                2 -> ItemSelectForm.send(player)
            }
        }
    }
}