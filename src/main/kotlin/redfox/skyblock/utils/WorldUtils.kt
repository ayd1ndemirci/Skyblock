package redfox.skyblock.utils

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.level.Level
import java.io.File

object WorldUtils {

    fun removeWorld(name: String, rmdir: Boolean = true): Int {
        val server = Server.getInstance()

        if (server.isLevelLoaded(name)) {
            val world = getWorldByNameNonNull(name)
            if (world.players.isNotEmpty()) {
                val spawnLocation = getDefaultWorldNonNull().spawnLocation
                for (entity in world.players) {
                    val player = entity as? Player ?: continue
                    player.teleport(spawnLocation)
                }

            }
            server.unloadLevel(world)
        }

        val worldPath = File(server.dataPath, "worlds/$name")
        var removedFiles = 1

        if (worldPath.exists()) {
            val files = worldPath.walkBottomUp()
            for (file in files) {
                if (file.isFile) {
                    if (file.delete()) removedFiles++
                } else if (file.isDirectory) {
                    if (file.delete()) removedFiles++
                }
            }
            if (rmdir) {
                worldPath.delete()
            }
        }

        return removedFiles
    }

    fun getWorldByNameNonNull(name: String): Level {
        val world = Server.getInstance().getLevelByName(name)
        return world ?: throw IllegalStateException("Required world $name is null")
    }

    fun getDefaultWorldNonNull(): Level {
        val world = Server.getInstance().defaultLevel
        return world ?: throw IllegalStateException("Default world is null")
    }
}
