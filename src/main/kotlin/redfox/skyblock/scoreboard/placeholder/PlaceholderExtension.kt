package redfox.skyblock.scoreboard.placeholder

import cn.nukkit.Player
import cn.nukkit.event.Listener
import cn.nukkit.plugin.Plugin

abstract class PlaceholderExtension : Listener {
    abstract fun getOwnedPlugin(): Plugin

    abstract fun getIdentifier(): String

    abstract fun onRequest(player: Player, params: String) : String?
}