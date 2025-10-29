package redfox.skyblock.form.faction

import cn.nukkit.Player
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.data.Factions
import redfox.skyblock.form.general.ListingForm

object FactionSearchResultsForm {
    fun send(player: Player, query: String, back: (() -> Unit)?, pageGot: Int = 0) {
        var page = pageGot
        val pageCount = Factions.getFactionSearchPageCount(query, page)
        if (page < 0) page = 0
        if (page >= pageCount) page = pageCount - 1

        ListingForm.send(
            player,
            SimpleForm("Klan Arama Sonuçları"),
            Factions.searchFactions(query, page),
            page,
            pageCount,
            "Bu isimle hiçbir klan bulunamadı.",
            { faction, _ -> "${faction.first} §7(${faction.second}PW)" },
            { FactionInfoForm.send(player, it.first) { send(player, query, back, page) } },
            { send(player, query, back, it) }, back
        )
    }
}