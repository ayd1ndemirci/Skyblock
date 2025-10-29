package redfox.skyblock.form.general

import cn.nukkit.Player
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.response.SimpleResponse
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.data.Database
import redfox.skyblock.utils.RankUtil
import redfox.skyblock.utils.Utils

object RankForm {

    fun send(player: Player) {
        val currentRank = Database.getRank(player.name)
        val nextRank = RankUtil.getNextRank(currentRank)
        val nextRankInfo = RankUtil.getRankInfo(nextRank)

        val price = nextRankInfo["price"] as? Int ?: 0
        val timeSeconds = nextRankInfo["time"] as? Int ?: 0
        val days = timeSeconds / 86400
        val hours = (timeSeconds % 86400) / 3600
        val minutes = (timeSeconds % 3600) / 60

        val timeString = Utils.getPlayTimeConverter(player.name)

        val content = buildString {
            append("§eMevcut Rütben: §f$currentRank\n")
            append("§eTerfi Olacağın Rütbe: §f$nextRank\n")
            append("§eRütbe Ücreti: §f$price RF\n")
            append("§eGerekenSüre: §e${days} gün ${hours} saat ${minutes} dakika\n")
            append("§aOyunda Kaldığın Toplam Süre: §2$timeString\n")
        }

        val form = SimpleForm("Rütbe Menüsü")
        form.addElement(ElementLabel(content))

        if (currentRank != "Prime") {
            form.addElement(ElementButton("Rütbe Atla"))
        } else {
            form.addElement(ElementButton("Kapat"))
        }

        form.send(player)

        form.onSubmit { _, response: SimpleResponse? ->
            if (response == null) return@onSubmit

            if (currentRank != "Prime" && response.buttonId() == 0) {
                RankUtil.skipRank(player)
                Utils.sound(player, "note.harp")
            }
        }
    }
}
