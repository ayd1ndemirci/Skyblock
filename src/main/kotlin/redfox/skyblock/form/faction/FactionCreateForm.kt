package redfox.skyblock.form.faction

import cn.nukkit.Player
import cn.nukkit.form.element.custom.ElementInput
import cn.nukkit.form.response.CustomResponse
import cn.nukkit.form.window.CustomForm
import redfox.skyblock.data.Factions

object FactionCreateForm {
    fun send(player: Player, back: (() -> Unit)?) {
        val form = CustomForm("Klan Oluştur")
        form.addElement(ElementInput("§aKlan ismi:", "Örnek: RedFox"))
        form.send(player)

        form.onSubmit { _: Player?, response: CustomResponse? ->
            if (response == null) return@onSubmit
            val name = response.getInputResponse(0).trim()
            if (Factions.getMemberFactionName(player.name) != null) {
                back?.invoke()
                return@onSubmit
            }
            Factions.create(name, player.name)
        }
    }
}