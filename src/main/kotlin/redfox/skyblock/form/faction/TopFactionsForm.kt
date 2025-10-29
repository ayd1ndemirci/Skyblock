package redfox.skyblock.form.faction

import cn.nukkit.Player
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.data.Factions
import redfox.skyblock.form.general.ListingForm

object TopFactionsForm {
    fun send(player: Player, pageGot: Int = 0, back: (() -> Unit)?) {
        var page = pageGot
        val pageCount = Factions.pageCount()
        if (page < 0) page = 0
        if (page >= pageCount) page = pageCount - 1

        ListingForm.send(
            player,
            SimpleForm("En Güçlü Klanlar"),
            Factions.top(page), page, pageCount,
            "Hiç klan oluşturulmamış.",
            { faction, _ -> "${faction.first} §7(${faction.second}PW)" },
            { FactionInfoForm.send(player, it.first) { send(player, page, back) } },
            { send(player, it, back) }, back
        )
    }
}