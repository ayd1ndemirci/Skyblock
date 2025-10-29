package redfox.skyblock.event

import cn.nukkit.event.EventHandler
import cn.nukkit.event.Listener
import cn.nukkit.event.player.PlayerJoinEvent
import cn.nukkit.event.player.PlayerQuitEvent
import redfox.skyblock.data.Database
import redfox.skyblock.form.general.AnniversaryForm
import java.util.*

class OtherListener : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val name = player.name.lowercase()

        val firstJoinUnix = Database.getFirstJoinUnix(name) ?: return
        val lastRewardUnix = Database.getLastAnniversaryRewardUnix(name) ?: 0L

        val calendarNow = Calendar.getInstance()
        val calendarFirstJoin = Calendar.getInstance().apply {
            timeInMillis = firstJoinUnix * 1000
        }

        val nowDay = calendarNow.get(Calendar.DAY_OF_MONTH)
        val nowMonth = calendarNow.get(Calendar.MONTH)
        val lastRewardYear = Calendar.getInstance().apply {
            timeInMillis = lastRewardUnix * 1000
        }.get(Calendar.YEAR)

        val currentYear = calendarNow.get(Calendar.YEAR)

        if (nowDay == calendarFirstJoin.get(Calendar.DAY_OF_MONTH) &&
            nowMonth == calendarFirstJoin.get(Calendar.MONTH) &&
            lastRewardYear < currentYear
        ) {
            AnniversaryForm.send(player)
        }
    }
}