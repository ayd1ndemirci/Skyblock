package redfox.skyblock.form.faction

import cn.nukkit.Player
import cn.nukkit.form.element.custom.ElementToggle
import cn.nukkit.form.response.CustomResponse
import cn.nukkit.form.window.CustomForm
import redfox.skyblock.data.Factions
import redfox.skyblock.data.Factions.FactionMemberRole

object FactionBanForm {
    fun send(player: Player, target: String, back: (() -> Unit)?) {
        val member = Factions.getMember(player)
        if (member == null) return NoFactionMainForm.send(player)
        val role = member.getInteger("role")
        if (role < FactionMemberRole.OFFICER.ordinal) return FactionMemberMainForm.send(player)
        val factionName = member.getString("factionName")
        val form = CustomForm("Klan Üyesini Yasaklama Onayı")
        val targetMember = Factions.getMember(target)
        if (targetMember != null
            && targetMember.getString("factionName") == factionName
            && targetMember.getInteger("role") >= role
        ) {
            if (back != null) back()
            else player.sendMessage("§cSenden yüksek mertebedeki bir klan üyesini yasaklayamazsın.")
            return
        }
        form.addElement(
            ElementToggle(
                "§e§l$target §r§eadlı oyuncuyu bulunduğun §a$factionName§e adlı klandan yasaklamak istediğinden emin misin?",
                false
            )
        )
        form.send(player)

        form.onSubmit { _: Player?, response: CustomResponse? ->
            if (response == null || !FactionsForm.requireOfficer(player)) return@onSubmit
            if (response.getToggleResponse(0)) {
                val targetMember = Factions.getMember(target)
                val targetFactionName = targetMember?.getString("factionName")
                if (targetMember != null
                    && targetFactionName == factionName
                    && targetMember.getInteger("role") >= role
                ) {
                    if (back != null) back()
                    else player.sendMessage("§cSenden yüksek mertebedeki bir klan üyesini yasaklayamazsın.")
                    return@onSubmit
                }

                if (targetFactionName == factionName) {
                    Factions.removeMember(target)
                    val targetPlayer = player.server.getPlayerExact(target)
                    targetPlayer?.sendMessage("§e$factionName§c adlı klandan yasaklandın.")
                }

                player.sendMessage("§e$target§a adlı oyuncu klandan yasaklandı. Artık klana katılma isteği atamaz.")
            } else back?.invoke()
        }
    }
}