package redfox.skyblock.form.faction

import cn.nukkit.Player
import cn.nukkit.form.element.custom.ElementToggle
import cn.nukkit.form.response.CustomResponse
import cn.nukkit.form.window.CustomForm
import redfox.skyblock.data.Factions
import redfox.skyblock.data.Factions.FactionMemberRole
import redfox.skyblock.data.Factions.FactionRequestType

object FactionAllianceAnswerForm {
    fun send(player: Player, factionName: String, back: (() -> Unit)?) {
        val member = Factions.getMember(player)
        if (member == null) return NoFactionMainForm.send(player)
        if (member.getInteger("role") != FactionMemberRole.OWNER.ordinal) return FactionsForm.send(player)
        val selfFactionName = member.getString("factionName")
        val form = CustomForm("Klan Müttefiklik İsteği")
        form.addElement(
            ElementToggle(
                "§a$factionName §eadlı klanının, sahibi olduğun §a$selfFactionName§e adlı klanına attığı müttefiklik isteğini kabul ediyor musun?",
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
            if (!Factions.hasRequest(factionName, member.getString("factionName"), FactionRequestType.ALLIANCE)) {
                if (back != null) back()
                else player.sendMessage("§cMenünün içindeyken karşı taraf müttefik isteğini iptal etti.")
                return@onSubmit
            }
            Factions.removeRequest(factionName, member.getString("factionName"), FactionRequestType.ALLIANCE)
            if (response.getToggleResponse(0)) {
                Factions.addAlliance(member.getString("factionName"), factionName)
                player.sendMessage("§eArtık §a$factionName §eadlı klan ile müttefiksiniz.")
                back?.invoke()
            } else {
                back?.invoke()
            }
        }
    }
}