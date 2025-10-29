package redfox.skyblock.task

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.scheduler.Task
import redfox.skyblock.data.Database
import redfox.skyblock.utils.Utils

class AutoBroadcastTask : Task() {

    private val messages = listOf(
        "§e*\n§e* §aRedFox Discord sunucusuna katılarak diğer oyuncular ile mesajlaşabilir özel etkinliklerden haberdar olabilir ve diğer oyuncular ile sesli konuşabilirsiniz.\n§e* §aredfoxmc.com/discord adresine girerek hemen katıl!\n§e*",
        "§e*\n§e* §6Korkunun ecele faydası yok ama arenanın sana faydası var.\n§e*§b Hemen '§g/arena§b' ile kılıcının gücünü göster!\n§e*",
        "§e*\n§e* §bDiscord sunucumuza hala katılmadın mı? Hemen sen de aramıza katıl ve sunucu hakkında olan tüm haberlerden haberdar ol!\n§e* §aredfoxmc.com/discord adresine girerek hemen katıl!\n§e*",
        "§e*\n§e* §6Sanki güzel başlangıç için güzel aletler lazım galiba RedFox'un sunduğu kitleri kullanman gerek gibi, '§b/kit§a' emrinde!\n§e*",
        "§e*\n§e* §bSunucumuza her gün oy verip birbirinden güzel eşyalar kazanabilirsin.\n§e* §aAy sonu en çok oy veren ilk 3 kişiye ödüller veriliyor!\n§e* §6Oy vermek için §g'/oy§6' yaz.\n§e*",
        "§e*\n§e* §cSkyblock hakkında tüm bilgileri görmek için '§g/yardim§c' yaz!\n§e*",
        "§e*\n§e* §aİhale sistemi ile eşyalarını açık arttırmaya koyabilir ve tüm oyuncularla alışveriş yaparsın.\n§e* Hemen '§a/ihale§e' komutunu incele!\n§e*",
    )


    override fun onRun(currentTick: Int) {
        val players = Server.getInstance().onlinePlayers.values
        val randomMessage = messages.random()

        for (player in players) {
            if (isNoticesEnabled(player)) {
                player.sendMessage(randomMessage)
                Server.getInstance().levels.values.forEach { level ->
                    level.players.values.forEach { onlinePlayer ->
                        Utils.sound(onlinePlayer, "note.harp")
                    }
                }
            }
        }
    }

    private fun isNoticesEnabled(player: Player): Boolean {
        return Database.isSettingEnabled(player.name, "notices")
    }
}
