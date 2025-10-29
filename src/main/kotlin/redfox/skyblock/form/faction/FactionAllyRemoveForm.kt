package redfox.skyblock.form.faction

import cn.nukkit.Player
import cn.nukkit.form.element.custom.ElementToggle
import cn.nukkit.form.response.CustomResponse
import cn.nukkit.form.window.CustomForm
import redfox.skyblock.data.Factions
import redfox.skyblock.data.Factions.FactionMemberRole

object FactionAllyRemoveForm {
    fun send(player: Player, factionName: String, back: (() -> Unit)?) {
        val member = Factions.getMember(player)
        if (member == null) return NoFactionMainForm.send(player)
        if (member.getInteger("role") != FactionMemberRole.OWNER.ordinal) return FactionsForm.send(player)
        val selfFactionName = member.getString("factionName")
        val form = CustomForm("Klan Müttefikliğini Kaldırma")
        form.addElement(
            ElementToggle(
                "§a$factionName §eadlı klanının, sahibi olduğun §a$selfFactionName§e adlı klanın ile olan müttefikliğini kaldırmak istiyor musun?",
                false
            )
        )
        form.send(player)

        form.onSubmit { _: Player?, response: CustomResponse? ->
            if (response == null) return@onSubmit
            val member = Factions.getMember(player)
            if (member == null) {
                NoFactionMainForm.send(player)
                return@onSubmit
            }
            if (member.getInteger("role") != FactionMemberRole.OWNER.ordinal) {
                FactionsForm.send(player)
                return@onSubmit
            }
            if (!Factions.areAllies(member.getString("factionName"), factionName)) {
                if (back != null) back()
                else player.sendMessage("§cMenünün içindeyken karşı taraf müttefikliği kaldırdı.")
                return@onSubmit
            }
            if (response.getToggleResponse(0)) {
                Factions.removeAlliance(member.getString("factionName"), factionName)
                player.sendMessage("§a$factionName §eadlı klanın ile olan müttefikliğin kaldırıldı.")
                if (back != null) back() else FactionsForm.send(player)
            } else back?.invoke()
        }
    }
}