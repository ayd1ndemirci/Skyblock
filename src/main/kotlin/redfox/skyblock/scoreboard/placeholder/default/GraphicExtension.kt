package redfox.skyblock.scoreboard.placeholder.default

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.event.EventHandler
import cn.nukkit.event.EventPriority
import cn.nukkit.event.player.PlayerItemHeldEvent
import cn.nukkit.event.player.PlayerJoinEvent
import cn.nukkit.event.player.PlayerQuitEvent
import cn.nukkit.plugin.Plugin
import redfox.skyblock.data.Database
import redfox.skyblock.group.GroupManager
import redfox.skyblock.scoreboard.placeholder.PlaceholderExtension
import redfox.skyblock.scoreboard.player.ScorePlayerManager
import redfox.skyblock.utils.RankUtil

class GraphicExtension(private val ownedPlugin: Plugin) : PlaceholderExtension() {
    override fun getOwnedPlugin(): Plugin = ownedPlugin

    override fun getIdentifier(): String = "graphic"

    @EventHandler(priority = EventPriority.NORMAL)
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        onPlaceholderUpdate(event.player)
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        onPlaceholderUpdate(event.player)
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onPlayerItemHeldEvent(event: PlayerItemHeldEvent) {
        onPlaceholderUpdate(event.player)
    }

    private fun onPlaceholderUpdate(player: Player) {
        val session = ScorePlayerManager.getPlayer(player) ?: return
        session.update("player_name")
        session.update("held_item_name")
        session.update("held_item_count")
        session.update("online")
        session.update("online_max")
    }

    override fun onRequest(player: Player, params: String): String? {
        return when (params) {
            "player" -> player.name
            "held_item_name" -> player.inventory.itemInHand.displayName
            "held_item_count" -> player.inventory.itemInHand.count.toString()
            "online" -> Server.getInstance().onlinePlayers.size.toString()
            "online_max" -> Server.getInstance().maxPlayers.toString()
            "world" -> player.level.folderName
            "level" -> "0"
            "rank" ->  Database.getRank(player.name).toString()
            "tag" -> GroupManager.getGroup(player.name).toString()
            "money" -> Database.getMoney(player.name).toString()
            "ping" -> player.ping.toString()
            "tps" -> Server.getInstance().ticksPerSecond.toString()
            else -> null
        }
    }
}
