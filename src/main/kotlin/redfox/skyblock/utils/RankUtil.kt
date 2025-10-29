package redfox.skyblock.utils

import cn.nukkit.Player
import cn.nukkit.form.window.ModalForm
import cn.nukkit.utils.TextFormat
import redfox.skyblock.data.Database
import redfox.skyblock.form.general.RankForm

object RankUtil {

    fun getRankExpire(name: String): Long {
        val doc = Database.get(name.lowercase()) ?: return -1L
        return doc.getLong("rankExpire") ?: -1L
    }

    fun getRanks(): List<String> {
        return listOf("Nova", "Meta", "Prime")
    }

    fun getRankInfo(rank: String): Map<String, Any> {
        return when (rank) {
            "Nova" -> mapOf(
                "price" to 1_000_000,
                "time" to 1 * 24 * 60 * 60,
                "allow" to "§7- §6/feed komutunu kullanabilme\n§7- §62x maden şansı"
            )

            "Meta" -> mapOf(
                "price" to 2_500_000,
                "time" to 2 * 24 * 60 * 60,
                "allow" to "§7- §6/feed komutunu kullanabilme\n§7- §6/can komutunu kullanabilme\n§7- §64x maden şansı"
            )

            "Prime" -> mapOf(
                "price" to 5_000_000,
                "time" to 7 * 24 * 60 * 60,
                "allow" to "§7- §6/feed komutunu kullanabilme\n§7- §6/can komutunu kullanabilme\n§7- §6/fly komutunu kullanabilme\n§7- §6/tamir komutu ile ücretsiz tamir basabilme\n§7- §66x maden şansı"
            )

            else -> emptyMap()
        }
    }

    fun getNextRank(rank: String?): String {
        val ranks = getRanks()
        val index = ranks.indexOf(rank)
        return if (index != -1 && index < ranks.size - 1) ranks[index + 1] else "Nova"
    }

    fun getEmojiRank(rank: String): String {
        return when (rank) {
            "Nova" -> ""
            "Meta" -> ""
            "Prime" -> ""
            else -> "Yok"
        }
    }

    fun skipRank(player: Player) {
        val name = player.name
        val currentRank = Database.getRank(name)
        val nextRank = getNextRank(currentRank)

        if (nextRank.isEmpty()) {
            player.sendMessage("${TextFormat.RED}Zaten en yüksek rütbedesin.")
            return
        }

        val info = getRankInfo(nextRank)

        val price = info["price"] as? Int ?: run {
            player.sendMessage("${TextFormat.RED}Bir sonraki rütbe bilgileri eksik.")
            return
        }
        val requiredTime = info["time"] as? Int ?: 0

        val money = Database.getMoney(name)
        val playTime = (Database.get(name)?.getInteger("playTime")) ?: 0
        val errors = mutableListOf<String>()

        if (money < price) {
            errors.add("§cYeterli paran yok. Gereken: ${price - money} RF.")
        }

        if (playTime < requiredTime) {
            val remaining = requiredTime - playTime
            val days = remaining / 86400
            val hours = (remaining % 86400) / 3600
            val minutes = (remaining % 3600) / 60
            errors.add("§cYeterli oyun süren yok. Gereken: $days gün $hours saat $minutes dakika")
        }

        if (errors.isNotEmpty()) {
            val msg = "§cRütbe atlamak için eksikler:\n" + errors.joinToString("\n") { "§7- $it" }

            ModalForm("Rütbe Menüsü")
                .content(msg)
                .yesText("Geri Dön")
                .noText("Kapat")
                .onYes { _ ->
                    RankForm.send(player)
                }
                .send(player)

            return
        }
        val expire = System.currentTimeMillis() / 1000 + (getRankInfo(nextRank)["time"] as Int)
        Database.removeMoney(name, price)
        Database.setRank(name, nextRank, expire)
        player.sendMessage("${TextFormat.GREEN}Başarıyla §2${nextRank} §arütbesine geçtin. Tagını değiştirmek için §7/tag §ayaz.")
    }

}
