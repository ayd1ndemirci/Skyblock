package redfox.skyblock.form.bounty

import cn.nukkit.Player
import cn.nukkit.form.window.SimpleForm
import org.bson.Document
import redfox.skyblock.data.Bounty
import redfox.skyblock.data.Bounty.BountyViewMode
import redfox.skyblock.form.general.ListingForm

object BountyOfferViewForm {
    fun send(player: Player, mode: BountyViewMode, gotPage: Int = 0, target: String = player.name) {
        val pageCount = Bounty.getPageCount(mode, player.name)
        var page = gotPage
        if (page < 0) page = 0
        if (page >= pageCount) page = pageCount - 1

        val title = when (mode) {
            BountyViewMode.ALL -> "Tüm Teklifler"
            BountyViewMode.WARLORD -> "Verdiğim Teklifler"
            BountyViewMode.TARGET -> if (player.name == target) "Kelleme Koyulmuş Teklifler" else "Kellesinin Teklifleri - $target"
        }

        val offers = Bounty.getAll(page, mode, player.name)

        ListingForm.send(
            player,
            SimpleForm("Kelle Avcısı - $title"),
            offers,
            page,
            pageCount,
            "Hiç teklif bulunamadı.",
            { bounty: Document, _ ->
                // reward int olarak geldiği için stringe çevirelim
                val targetName = bounty.getString("target") ?: "Bilinmiyor"
                val rewardInt = bounty.getInteger("reward", 0)
                val rewardStr = rewardInt.toString()
                val warlordName = bounty.getString("warlord") ?: "Bilinmiyor"

                "Hedef: $targetName - Ödül: $rewardStr\n" + (
                        if (mode == BountyViewMode.WARLORD) "Silmek için tıkla"
                        else "Parayı veren: $warlordName"
                        )
            },
            { BountyOfferRemoveForm.send(player, it) },
            { send(player, mode, it) },
            { BountyForm.send(player) }
        )
    }
}
