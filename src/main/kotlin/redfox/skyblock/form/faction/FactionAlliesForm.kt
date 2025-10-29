package redfox.skyblock.form.faction

import cn.nukkit.Player
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.data.Factions
import redfox.skyblock.form.general.ListingForm
import redfox.skyblock.utils.Utils

object FactionAlliesForm {
    fun send(player: Player, pageGot: Int = 0, back: (() -> Unit)?) {
        val member = Factions.getMember(player)
        if (member == null) return NoFactionMainForm.send(player)
        var page = pageGot
        val alliances = Factions.getAlliances(member.getString("factionName"))
        val pageCount = Utils.pageCount(alliances.size)
        if (page < 0) page = 0
        if (page >= pageCount) page = pageCount - 1

        ListingForm.send(
            player,
            SimpleForm("Klan Müttefikleri"),
            Utils.page(alliances, page), page, pageCount,
            "Klanının müttefik olduğu bir klan yok.",
            { faction, _ -> "$faction §7(${Factions.get(faction)?.getInteger("power")}PW)" },
            { FactionInfoForm.send(player, it) { send(player, page, back) } },
            { send(player, it, back) }, back
        )
    }
}