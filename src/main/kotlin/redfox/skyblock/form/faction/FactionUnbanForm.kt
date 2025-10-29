package redfox.skyblock.form.faction

import cn.nukkit.Player
import cn.nukkit.form.element.custom.ElementToggle
import cn.nukkit.form.response.CustomResponse
import cn.nukkit.form.window.CustomForm
import redfox.skyblock.data.Factions
import redfox.skyblock.data.Factions.FactionMemberRole

object FactionUnbanForm {
    fun send(player: Player, target: String, back: (() -> Unit)?) {
        val member = Factions.getMember(player)
        if (member == null) return NoFactionMainForm.send(player)
        if (member.getInteger("role") < FactionMemberRole.OFFICER.ordinal) return FactionMemberMainForm.send(player)
        val factionName = member.getString("factionName")
        val form = CustomForm("Oyuncunun Klan Yasağını Kaldırma Onayı")
        form.addElement(
            ElementToggle(
                "§e§l$target §r§eadlı oyuncuyu bulunduğun §a$factionName§e adlı klandaki oyuncu yasağını kaldırmayı onaylıyor musun?",
                false
            )
        )
        form.send(player)

        form.onSubmit { _: Player?, response: CustomResponse? ->
            if (response == null || !FactionsForm.requireOfficer(player)) return@onSubmit
            if (response.getToggleResponse(0)) {
                if (Factions.removeBan(factionName, target)) {
                    player.sendMessage("§cBu oyuncunun yasağı sen menüdeyken başkası tarafından kaldırılmış.")
                } else {
                    player.sendMessage("§e$target§a adlı oyuncunun klan yasağı kaldırıldı. Artık klana katılma isteği gönderebilir.")
                }
            } else back?.invoke()
        }
    }
}