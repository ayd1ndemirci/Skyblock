package redfox.skyblock.utils

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.level.Position

object Location {

    const val LOBBY: String = "lobi"
    const val ARENA: String = "arena"
    const val NETHER: String = "arena"
    const val END: String = "arena"

    fun teleportWorld(player: Player, worldName: String): Boolean {
        val level = Server.getInstance().getLevelByName(worldName) ?: return false
        return player.teleport(level.spawnLocation)
    }

    fun playerCount(worldName: String): Int {
        val level = Server.getInstance().getLevelByName(worldName) ?: return 0
        return level.players.size
    }

    fun getPlayerLocation(player: Player): Position {
        return player.location
    }

    fun createPosition(x: Double, y: Double, z: Double, worldName: String): Position? {
        val level = Server.getInstance().getLevelByName(worldName) ?: return null
        return Position(x, y, z, level)
    }
}
