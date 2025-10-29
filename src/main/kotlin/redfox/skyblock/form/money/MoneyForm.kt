package redfox.skyblock.form.money

import cn.nukkit.Player
import cn.nukkit.form.element.simple.ButtonImage
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.response.SimpleResponse
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.data.Database

object MoneyForm {
    fun send(player: Player) {
        val money = Database.getMoney(player)
        val form = SimpleForm("Para Menü")
        form.addElement(
            ElementButton(
                "Paran: §r§o$money RF",
                ButtonImage(ButtonImage.Type.PATH, "textures/ui/MCoin.png")
            )
        )
        form.addElement(ElementButton("Zenginler", ButtonImage(ButtonImage.Type.PATH, "textures/ui/op.png")))
        form.addElement(ElementButton("Para Gönder", ButtonImage(ButtonImage.Type.PATH, "textures/ui/trade_icon.png")))
        form.addElement(
            ElementButton(
                "Başkasının Parasına Bak",
                ButtonImage(ButtonImage.Type.PATH, "textures/ui/magnifyingGlass.png")
            )
        )

        form.send(player)

        form.onSubmit { _: Player?, response: SimpleResponse? ->
            if (response == null) return@onSubmit

            when (response.buttonId()) {
                1 -> TopMoneyForm.send(player) { send(player) }
                2 -> GiveMoneyForm.send(player)
                3 -> SeeMoneyForm.send(player)
            }
        }
    }
}