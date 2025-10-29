package redfox.skyblock.event.custom

import cn.nukkit.Player
import cn.nukkit.event.Event
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Updates.set
import org.bson.Document
import redfox.skyblock.data.IslandDB

class IslandVisitStatusEvent(
    private var player: Player,
    private val partner: Boolean
) : Event() {

    fun call() {
        var playerName = player.name
        if (partner) {
            playerName = IslandDB.getPlayerPartner(playerName) ?: return
        }
        val island = IslandDB.getIsland(playerName) ?: return
        val visitDoc = island.get("visit", Document::class.java) ?: Document()
        val status = visitDoc.getBoolean("status", false)
        visitDoc["status"] = !status
        IslandDB.collection.updateOne(
            eq("player", playerName),
            set("visit", visitDoc)
        )
        if (!status) {
            player.sendMessage("§aAda ziyaret durumun açık hale getirildi.")
        } else player.sendMessage("§aAda ziyaret durumun kapalı hale getirildi.")
    }

    fun getPlayer(): Player = player

    fun setPlayer(player: Player) {
        this.player = player
    }
}
