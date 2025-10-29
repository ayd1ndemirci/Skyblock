package redfox.skyblock.form.faction

import cn.nukkit.Player
import cn.nukkit.form.element.custom.ElementInput
import cn.nukkit.form.response.CustomResponse
import cn.nukkit.form.window.CustomForm

object FactionSearchForm {
    fun send(player: Player, back: (() -> Unit)?) {
        val form = CustomForm("Klan Ara")
        form.addElement(ElementInput("Aradığın klanın ismini gir:", "Örnek: RedFox"))
        form.send(player)

        form.onSubmit { _: Player?, response: CustomResponse? ->
            if (response == null) return@onSubmit
            val query = response.getInputResponse(0).trim()
            if (query.isBlank()) {
                if (back != null) back() else player.sendMessage("§cArama kısmını boş bıraktın.")
            } else FactionSearchResultsForm.send(player, query, back)
        }
    }
}