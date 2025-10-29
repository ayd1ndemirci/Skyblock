package redfox.skyblock.form.faction

import cn.nukkit.Player
import cn.nukkit.form.element.custom.ElementToggle
import cn.nukkit.form.response.CustomResponse
import cn.nukkit.form.window.CustomForm
import redfox.skyblock.data.Factions

object FactionLeaveForm {
    fun send(player: Player, back: (() -> Unit)?) {
        val factionName = Factions.getMember(player)?.getString("factionName")
        if (factionName == null) return NoFactionMainForm.send(player)
        val form = CustomForm("Klandan Ayrılma Onayı")
        form.addElement(ElementToggle("§e§l${factionName} §r§eadlı klandan ayrılmak istediğinden emin misin?", false))
        form.send(player)

        form.onSubmit { _: Player?, response: CustomResponse? ->
            if (response == null) return@onSubmit
            if (response.getToggleResponse(0)) {
                if (Factions.removeMember(player.name)) player.sendMessage("§aKlandan ayrıldın.")
                else player.sendMessage("§cNasıl yaptın bilmiyorum, ama bir klanda değilsin.")
            } else back?.invoke()
        }
    }
}