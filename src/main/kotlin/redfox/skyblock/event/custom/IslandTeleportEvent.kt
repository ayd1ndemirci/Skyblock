package redfox.skyblock.listener.event

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.event.Event
import cn.nukkit.level.Level
import cn.nukkit.level.Position
import redfox.skyblock.Core
import redfox.skyblock.data.IslandDB

class IslandTeleportEvent(
    val player: Player,
    world: Level? = null,
    val partner: Boolean = false
) : Event() {

    var world: Level

    init {
        try {
            var playerName = player.name
            if (partner) {
                IslandDB.getPlayerPartner(playerName)?.let {
                    playerName = it
                }
            }
            this.world = world ?: run {
                if (!Server.getInstance().isLevelLoaded(playerName)) {
                    Server.getInstance().loadLevel(playerName)
                }
                Server.getInstance().getLevelByName(playerName)!!
            }
        } catch (ex: Exception) {
            player.sendMessage("§cBir hata oluştu lütfen tekrar deneyiniz.")
            Core.instance.logger.info("IslandTeleportEvent Exception: Line ${ex.stackTrace.firstOrNull()?.lineNumber} Error: ${ex.message}")
            this.world = player.level
        }
    }

    fun call() {
        if (!Server.getInstance().isLevelLoaded(world.name)) {
            Server.getInstance().loadLevel(world.name)
        }
        val spawn = world.spawnLocation
        player.teleport(Position(spawn.x, spawn.y, spawn.z, spawn.level))
        player.sendMessage("§aAdanıza ışınlandınız.")
    }
}
