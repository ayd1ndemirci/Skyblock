package redfox.skyblock.event.custom

import cn.nukkit.Player
import cn.nukkit.event.Event
import cn.nukkit.event.HandlerList

class IslandLevelChangeEvent(
    var player: Player,
    var oldLevel: Int,
    var newLevel: Int,
    var islandName: String
) : Event() {

    companion object {
        @JvmStatic
        private val handlers = HandlerList()

        @JvmStatic
        fun getHandlers(): HandlerList = handlers
    }
}
