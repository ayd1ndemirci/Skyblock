package redfox.skyblock.form.auction

import cn.nukkit.Player
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.window.SimpleForm

object AuctionInfoForm {

    fun send(player: Player) {
        val form = SimpleForm("İhale Bilgi")
        form.addElement(ElementLabel("Bilgi"))
        form.addElement(ElementButton("Geri"))
        form.send(player)
        form.onSubmit { _, response ->
            if (response == null) return@onSubmit
            if (response.buttonId() == 0) AuctionForm.send(player)
        }
    }
}