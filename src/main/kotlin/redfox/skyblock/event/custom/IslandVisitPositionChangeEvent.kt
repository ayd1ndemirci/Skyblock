package redfox.skyblock.event.custom

import cn.nukkit.Player
import cn.nukkit.event.Event
import cn.nukkit.event.HandlerList
import cn.nukkit.level.Position
import redfox.skyblock.data.IslandDB

class IslandVisitPositionChangeEvent(
    private var player: Player,
    private val partner: Boolean
) : Event() {

    private var position: Position = player.position

    fun getPlayer(): Player = player

    fun setPlayer(player: Player) {
        this.player = player
    }

    fun getPosition(): Position = position

    fun setPosition(position: Position) {
        this.position = position
    }

    fun execute() {
        var playerName = player.name
        if (partner) {
            playerName = IslandDB.getPlayerPartner(playerName) ?: run {
                player.sendMessage("§cPartner bilgisi alınamadı!")
                return
            }
        }

        if (player.level.folderName != playerName) {
            player.sendMessage("§cAda ziyaret noktanı değiştirmek için adanda olmalısın!")
            return
        }

        val visitData = mutableMapOf(
            "status" to true,
            "position" to "${position.x}:${position.y}:${position.z}"
        )

        IslandDB.collection.updateOne(
            org.bson.Document("player", playerName),
            com.mongodb.client.model.Updates.set("visit", org.bson.Document(visitData))
        )

        player.sendMessage("§aAda ziyaret pozisyonun başarıyla güncellendi.")
    }

    companion object {
        private val handlers = HandlerList()

        @JvmStatic
        fun getHandlers(): HandlerList = handlers
    }
}
