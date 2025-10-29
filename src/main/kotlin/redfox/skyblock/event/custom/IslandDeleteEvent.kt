package redfox.skyblock.event.custom

import cn.nukkit.Player
import cn.nukkit.event.Event
import cn.nukkit.event.HandlerList
import redfox.skyblock.data.IslandDB
import redfox.skyblock.utils.WorldUtils

class IslandDeleteEvent(val player: Player) : Event() {

    companion object {
        @JvmStatic
        private val handlers = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = handlers
    }

    fun call() {
        val playerName = player.name
        if (IslandDB.getIsland(playerName) != null) {
            val threeDaysLater = (System.currentTimeMillis() / 1000) + 1 * 24 * 3600
            IslandDB.updateDeleteTime(playerName, threeDaysLater)
        }
        WorldUtils.removeWorld(playerName)
        IslandDB.removeIsland(playerName)
        player.sendMessage("§cAdanız silindi.")
    }
}
