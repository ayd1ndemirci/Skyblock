package redfox.skyblock.event.custom

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.event.Event
import cn.nukkit.event.HandlerList
import cn.nukkit.level.Sound
import cn.nukkit.math.Vector3
import redfox.skyblock.data.IslandDB

class IslandSpawnPositionChangeEvent(
    private var player: Player,
    private val partner: Boolean = false
) : Event() {


    companion object {
        @JvmStatic
        private val handlers = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = handlers
    }

    fun call() {
        var playerName = player.name
        if (partner) {
            playerName = IslandDB.getPlayerPartner(playerName) ?: playerName
        }

        Server.getInstance().loadLevel(playerName)
        val world = Server.getInstance().getLevelByName(playerName) ?: return

        if (player.level.folderName != world.folderName) {
            player.sendMessage("§cBu işlemi yapmak için lütfen adana git.")
            return
        }

        val pos = player.position
        world.setSpawnLocation(Vector3(pos.x, pos.y, pos.z))
        player.level.addSound(player.position, Sound.RANDOM_LEVELUP, 1f, 1f, player)
        player.sendMessage("§aAda doğma noktan değiştirildi.")
    }

    fun getPlayer(): Player = player

    fun setPlayer(player: Player) {
        this.player = player
    }
}
