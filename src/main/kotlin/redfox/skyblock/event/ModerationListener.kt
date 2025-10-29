package redfox.skyblock.event

import cn.nukkit.event.EventHandler
import cn.nukkit.event.Listener
import cn.nukkit.event.player.PlayerJoinEvent
import cn.nukkit.event.player.PlayerLoginEvent
import redfox.skyblock.data.Ban
import redfox.skyblock.data.Tracker
import java.text.SimpleDateFormat
import java.util.*

class ModerationListener : Listener {

    @EventHandler
    fun onPlayerLogin(event: PlayerLoginEvent) {
        val player = event.player
        val name = player.name.lowercase()

        if (Ban.isBanned(name)) {
            val reason = Ban.getReason(name)
            val endMillis = Ban.getEnd(name)
            val endFormatted = if (endMillis == Long.MAX_VALUE) {
                "SINIRSIZ"
            } else {
                val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm")
                sdf.format(Date(endMillis))
            }

            val message = buildString {
                appendLine("§c§lSunucudan banlandınız!")
                appendLine()
                appendLine("§7Sebep: §f$reason")
                appendLine("§7Bitiş: §f$endFormatted")
            }

            event.setKickMessage(message.trimIndent())
            event.setCancelled(true)
        }
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        Tracker.trackPlayer(event.player)
    }
}
