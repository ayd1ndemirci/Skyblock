package redfox.skyblock.form.faction

import cn.nukkit.Player
import cn.nukkit.form.element.custom.ElementToggle
import cn.nukkit.form.response.CustomResponse
import cn.nukkit.form.window.CustomForm
import redfox.skyblock.data.Factions
import redfox.skyblock.data.Factions.FactionMemberRole

object FactionKickForm {
    fun send(player: Player, target: String, back: (() -> Unit)?) {
        val member = Factions.getMember(player)
        if (member == null) return NoFactionMainForm.send(player)
        val role = member.getInteger("role")
        if (role < FactionMemberRole.OFFICER.ordinal) return FactionMemberMainForm.send(player)
        val factionName = member.getString("factionName")
        val form = CustomForm("Klan Üyesini Atma Onayı")
        val targetMember = Factions.getMember(target)
        if (targetMember == null) {
            if (back != null) back()
            else player.sendMessage("§cAynı klanda olmadığın bir oyuncuyu atamazsın.")
            return
        }
        val targetMemberRole = targetMember.getInteger("role")
        if (targetMemberRole >= role) {
            if (back != null) back()
            else player.sendMessage("§cSenden yüksek mertebedeki bir klan üyesini atamazsın.")
            return
        }
        form.addElement(
            ElementToggle(
                "§e§l$target §r§eadlı klan üyesini bulunduğun §a$factionName§e adlı klandan atmak istediğinden emin misin?",
                false
            )
        )
        form.send(player)

        form.onSubmit { _: Player?, response: CustomResponse? ->
            if (response == null || !FactionsForm.requireOfficer(player)) return@onSubmit
            if (response.getToggleResponse(0)) {
                val targetMember = Factions.getMember(target)
                if (targetMember == null || targetMember.getString("factionName") != factionName) {
                    player.sendMessage("§cBu oyuncu sen menüdeyken başkası tarafından atılmış.")
                } else {
                    if (targetMember.getInteger("role") >= Factions.getMember(player)!!.getInteger("role")) {
                        if (back != null) back()
                        else player.sendMessage("§cSenden yüksek mertebedeki bir oyuncuyu atamazsın.")
                        return@onSubmit
                    }
                    Factions.removeMember(target)
                    val targetPlayer = player.server.getPlayerExact(target)
                    targetPlayer?.sendMessage("§e$factionName§c adlı klandan atıldın.")
                    player.sendMessage("§e$target§a adlı oyuncu klandan atıldı. Bu oyuncu hala klana katılma isteği gönderebilir.")
                }
            } else back?.invoke()
        }
    }
}