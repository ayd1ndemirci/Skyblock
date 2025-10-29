package redfox.skyblock.form.faction

import cn.nukkit.Player
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.response.SimpleResponse
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.data.Factions

object NoFactionMainForm {
    fun send(player: Player) {
        Factions.getMember(player)
        val form = SimpleForm("Klan Menüsü")
        form.addElement(ElementButton("Klan Oluştur"))
        form.addElement(ElementButton("Klan Ara"))
        form.addElement(ElementButton("En Güçlü Klanlar"))
        form.send(player)

        form.onSubmit { _: Player?, response: SimpleResponse? ->
            if (response == null) return@onSubmit
            when (response.buttonId()) {
                0 -> FactionCreateForm.send(player) { send(player) }
                1 -> FactionSearchForm.send(player) { send(player) }
                2 -> TopFactionsForm.send(player) { send(player) }
            }
        }
    }
}