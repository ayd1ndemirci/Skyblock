package redfox.skyblock.form.money

import cn.nukkit.Player
import cn.nukkit.form.element.custom.ElementDropdown
import cn.nukkit.form.response.CustomResponse
import cn.nukkit.form.window.CustomForm
import redfox.skyblock.Core
import redfox.skyblock.form.general.ProfileForm

object SeeMoneyForm {
    fun send(player: Player) {
        val form = CustomForm("Başkasının Parasına Bak")
        val players = Core.Companion.instance.server.onlinePlayers
            .filter { it.value.name != player.name }
            .map { it.value.name }
            .toMutableList()
        players.add(0, "§c§lSeçiniz")

        form.addElement(ElementDropdown("§eBakmak istediğin oyuncuyu seç:", players))
        form.send(player)

        form.onSubmit { _: Player?, response: CustomResponse? ->
            if (response == null) return@onSubmit
            val playerIndex = response.getDropdownResponse(0).elementId()
            if (playerIndex == 0) {
                return@onSubmit
            }
            val target = players[playerIndex]
            ProfileForm.send(player, target) { MoneyForm.send(player) }
        }
    }
}