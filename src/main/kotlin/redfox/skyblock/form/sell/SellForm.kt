package redfox.skyblock.form.sell

import cn.nukkit.Player
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.response.SimpleResponse
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.utils.SellUtils

object SellForm {

    fun send(player: Player) {
        val form = SimpleForm("Satış Menüsü")
        form.addElement(ElementButton("Elindeki İtemi Sat"))
        form.addElement(ElementButton("Tüm Envanteri Sat"))
        form.send(player)

        form.onSubmit { _: Player?, response: SimpleResponse? ->
            if (response == null) return@onSubmit

            when (response.buttonId()) {
                0 -> SellUtils.sellHand(player)
                1 -> SellUtils.sellAll(player)
            }
        }
    }
}
