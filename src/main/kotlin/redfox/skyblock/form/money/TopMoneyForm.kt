package redfox.skyblock.form.money

import cn.nukkit.Player
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.data.Database
import redfox.skyblock.form.general.ListingForm
import redfox.skyblock.form.general.ProfileForm

object TopMoneyForm {
    fun send(player: Player, pageGot: Int = 0, back: (() -> Unit)?) {
        var page = pageGot
        val pageCount = Database.moneyPageCount()
        if (page < 0) page = 0
        if (page >= pageCount) page = pageCount - 1
        val players = Database.topMoney(page)

        ListingForm.send(
            player,
            SimpleForm("Para Sıralaması"),
            players,
            page,
            pageCount,
            "Kimsenin parası yok?",
            { money, index -> "${index + 1 + page * 10}. ${money.first} - ${money.second}${if (money.first == player.name) " (Sen)" else ""}" },
            { ProfileForm.send(player, it.first) { send(player, page, back) } },
            { send(player, it, back) }, back
        )
    }
}