package redfox.skyblock.task

import cn.nukkit.Server
import cn.nukkit.scheduler.Task
import redfox.skyblock.data.Tag
import redfox.skyblock.event.custom.PlayerRankExpiredEvent
import java.util.*

class ExpireDateCheckTask : Task() {
    override fun onRun(currentTick: Int) {
        val onlinePlayers = Server.getInstance().onlinePlayers.values

        for (player in onlinePlayers) {
            val playerName = player.name.lowercase(Locale.ROOT)
            if (Tag.profileExists(playerName)) {
                val profile = Tag.getProfile(playerName)
                if (profile != null && System.currentTimeMillis() >= profile.time) {
                    val event = PlayerRankExpiredEvent(player)
                    Server.getInstance().pluginManager.callEvent(event)
                }
            }
        }
    }
}