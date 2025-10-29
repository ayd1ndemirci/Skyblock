package redfox.skyblock.form.faction

import cn.nukkit.Player
import cn.nukkit.form.element.custom.ElementToggle
import cn.nukkit.form.window.CustomForm
import redfox.skyblock.data.Factions
import redfox.skyblock.data.Factions.FactionMemberRole
import redfox.skyblock.data.Factions.FactionRequestType
import redfox.skyblock.utils.Utils

object FactionAllianceSendForm {
    fun send(player: Player, factionName: String, back: (() -> Unit)?) {
        val member = Factions.getMember(player)
        if (member == null) return NoFactionMainForm.send(player)
        if (member.getInteger("role") != FactionMemberRole.OWNER.ordinal) return FactionsForm.send(player)
        val selfFactionName = member.getString("factionName")
        val form = CustomForm("Klan Müttefiklik İsteği")
        form.addElement(
            ElementToggle(
                "§a$factionName §eadlı klanına, sahibi olduğun §a$selfFactionName§e adlı klanınla müttefik olması için istek atmak istediğinden emin misin?",
                false
            )
        )
        form.send(player)

        form.onSubmit { _, response ->
            if (response == null) return@onSubmit
            if (!response.getToggleResponse(0)) {
                back?.invoke()
                return@onSubmit
            }
            val faction = Factions.get(factionName)
            if (faction == null) {
                if (back != null) back()
                else player.sendMessage("§cKlan bulunamadı.")
                return@onSubmit
            }
            val member = Factions.getMember(player)
            if (member == null) {
                NoFactionMainForm.send(player)
                return@onSubmit
            }
            if (member.getInteger("role") != FactionMemberRole.OWNER.ordinal) {
                FactionsForm.send(player)
                return@onSubmit
            }
            val selfFactionName = factionName
            if (Factions.hasRequest(factionName, selfFactionName, FactionRequestType.ALLIANCE)
                || Factions.areAllies(selfFactionName, factionName)
            ) {
                back?.invoke()
                return@onSubmit
            }
            if (!Utils.checkCooldown("$selfFactionName\nally", 10000 * 60)) {
                player.sendMessage("§cBu klana yakın bir zamanda müttefiklik isteği attın, lütfen daha sonra tekrar dene.")
                return@onSubmit
            }
            if (Factions.addRequest(selfFactionName, factionName, FactionRequestType.ALLIANCE)) {
                player.sendMessage("§aKlan müttefiklik isteği başarıyla gönderildi.")
                player.server.getPlayerExact(faction.getString("owner"))
                    .sendMessage("§a${selfFactionName} adlı klan müttefiklik teklif ediyor.")
            } else back?.invoke()
        }
    }
}
