package redfox.skyblock.form.faction

import cn.nukkit.Player
import cn.nukkit.form.element.custom.ElementToggle
import cn.nukkit.form.response.CustomResponse
import cn.nukkit.form.window.CustomForm
import redfox.skyblock.data.Factions
import redfox.skyblock.data.Factions.FactionMemberRole
import redfox.skyblock.data.Factions.FactionRequestType

object FactionMemberAnswerForm {
    fun send(player: Player, target: String, back: (() -> Unit)?) {
        val member = Factions.getMember(player)
        if (member == null) return NoFactionMainForm.send(player)
        if (member.getInteger("role") < FactionMemberRole.OFFICER.ordinal) return FactionMemberMainForm.send(player)
        val factionName = member.getString("factionName")
        val form = CustomForm("Klan Katılma İsteği")
        form.addElement(
            ElementToggle(
                "§a$target §eadlı oyuncuyu, sahibi olduğun §a$factionName§e adlı klanına attığı katılma isteğini kabul ediyor musun?",
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
            if (member.getInteger("role") < FactionMemberRole.OFFICER.ordinal) {
                FactionsForm.send(player)
                return@onSubmit
            }
            val factionName = member.getString("factionName")
            if (!Factions.hasRequest(target, factionName, FactionRequestType.MEMBER)) {
                if (back != null) back()
                else player.sendMessage("§cMenünün içindeyken karşı taraf katılma isteğini iptal etti veya başka bir klana girdi.")
                return@onSubmit
            }
            Factions.removeMemberRequests(target)
            if (response.getToggleResponse(0)) {
                Factions.setMember(factionName, target, FactionMemberRole.MEMBER)
                player.sendMessage("§a$target §eadlı oyuncunun klana katılma isteği kabul edildi.")
                val targetPlayer = player.server.getPlayerExact(target)
                targetPlayer?.sendMessage("§e$factionName §aadlı klanına attığın katılma isteğin kabul edildi!")
            }

            back?.invoke()
        }
    }
}