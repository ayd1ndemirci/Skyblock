package redfox.skyblock

import cn.nukkit.event.Listener
import cn.nukkit.network.protocol.RemoveObjectivePacket
import cn.nukkit.network.protocol.SetDisplayObjectivePacket
import cn.nukkit.network.protocol.SetScorePacket
import cn.nukkit.plugin.PluginBase
import redfox.skyblock.data.IslandDB
import redfox.skyblock.manager.PermissionManager
import redfox.skyblock.manager.ServerManager
import redfox.skyblock.scoreboard.Scoreboard
import redfox.skyblock.scoreboard.placeholder.PlaceholderAPI
import redfox.skyblock.scoreboard.placeholder.default.GraphicExtension
import java.io.File
import java.util.*

class Core : PluginBase(), Listener {

    companion object {
        lateinit var instance: Core
            private set

        lateinit var dupeLogFile: File
            private set
        lateinit var defaultBoard: Scoreboard

        private val boards: MutableMap<String, Scoreboard> = mutableMapOf()
    }

    override fun onLoad() {
        instance = this
        saveDefaultConfig()
        saveResource("groups.yml")
    }

    override fun onEnable() {
        //Redis.setPlayerCount(0)
        ServerManager.run()
        initializeDupeLogFile()
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Istanbul"))

        config.getSection("boards").forEach { (boardName, _) ->
            val scoreboard = Scoreboard(
                name = boardName,
                title = config.getString("boards.$boardName.title"),
                lines = config.getStringList("boards.$boardName.lines")
            )
            boards[boardName] = scoreboard
        }
        val defaultBoardName = config.getString("default-board")
        val findDefaultBoard = boards[defaultBoardName]
        if (findDefaultBoard == null) {
            server.pluginManager.disablePlugin(this)
            logger.warning("No scoreboard defined with name $defaultBoardName found.")
            return
        }
        defaultBoard = findDefaultBoard

        PlaceholderAPI.register(GraphicExtension(this))
    }

    override fun onDisable() {
        //Redis.setPlayerCount(0)
        for ((worldName, blockCounts) in Island.blockCountCache) {
            IslandDB.saveBlockCounts(worldName, blockCounts)
        }
        try {
            PermissionManager.removePermissions()
        } catch (ex: Exception) {
            logger.warning("İzinler kaldırılırken hata: ${ex.message}")
        }
    }

    private fun initializeDupeLogFile() {
        dupeLogFile = File(dataFolder, "dupe_log.json")
        if (!dupeLogFile.exists()) {
            dupeLogFile.parentFile.mkdirs()
            dupeLogFile.writeText("{}")
        }
    }


    fun getDefaultBoard(): Scoreboard = defaultBoard

    fun getBoard(name: String): Scoreboard? = boards[name]

}
