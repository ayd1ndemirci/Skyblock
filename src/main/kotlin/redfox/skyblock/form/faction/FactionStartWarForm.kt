package redfox.skyblock.form.faction

import cn.nukkit.Player
import cn.nukkit.form.element.custom.ElementToggle
import cn.nukkit.form.window.CustomForm
import redfox.skyblock.data.Factions
import redfox.skyblock.data.Factions.FactionMemberRole
import redfox.skyblock.data.Factions.FactionRequestType
import redfox.skyblock.utils.Utils

object FactionStartWarForm {
    fun send(player: Player, factionName: String, back: (() -> Unit)?) {
        val member = Factions.getMember(player)
        if (member == null) return NoFactionMainForm.send(player)
        if (member.getInteger("role") != FactionMemberRole.OWNER.ordinal) return FactionsForm.send(player)
        val selfFactionName = member.getString("factionName")
        val form = CustomForm("Klan Müttefiklik İsteği")
        form.addElement(
            ElementToggle(
                "§a$factionName §eadlı klanına, sahibi olduğun §a$selfFactionName§e adlı klanın ile klan savaşına girme daveti atmak istediğinden emin misin?",
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
            if (Factions.warRequests.contains(Pair(factionName, selfFactionName))) {
                if (back != null) back()
                else player.sendMessage("§cBu klana zaten savaş isteği attın.")
                return@onSubmit
            }
            if (Factions.ongoingWar != null) {
                if (back != null) back()
                else player.sendMessage("§cŞu anda bir savaş devam ediyor, lütfen daha sonra tekrar dene.")
                return@onSubmit
            }
            if (!Utils.checkCooldown("$selfFactionName\n$factionName\nwar", 10000 * 60)) {
                player.sendMessage("§cBu klana yakın bir zamanda klan savaşı isteği attın, lütfen daha sonra tekrar dene.")
                return@onSubmit
            }

            Factions.warRequests.add(Pair(factionName, selfFactionName))

            player.sendMessage("§aKlan savaşı isteği başarıyla gönderildi.")
            player.server.getPlayerExact(faction.getString("owner"))
                .sendMessage("§a${selfFactionName} adlı klan senin klanına savaş açmak istiyor.")
        }
    }
}
