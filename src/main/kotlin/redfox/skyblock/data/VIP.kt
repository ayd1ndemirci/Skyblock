package redfox.skyblock.data

import cn.nukkit.Server
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import redfox.skyblock.data.MongoDB.db

object VIP {

    init {
        Configurator.setLevel("org.mongodb.driver", Level.OFF)
    }

    private val collection = db.getCollection("vips")

    fun addVIP(player: String, vipType: String, days: Int) {
        val now = System.currentTimeMillis() / 1000
        val addedTime = days * 86400L

        val existingVIP = collection.find(
            Filters.and(Filters.eq("player", player), Filters.eq("vipType", vipType))
        ).first()

        val newTime = if (existingVIP != null) {
            val currentTime = (existingVIP["time"] as? Number)?.toLong() ?: 0L
            if (currentTime > now) {
                currentTime + addedTime
            } else {
                now + addedTime
            }
        } else {
            now + addedTime
        }

        collection.updateOne(
            Filters.and(Filters.eq("player", player), Filters.eq("vipType", vipType)),
            Updates.combine(
                Updates.set("player", player.lowercase()),
                Updates.set("vipType", vipType),
                Updates.set("time", newTime.toInt())
            ),
            UpdateOptions().upsert(true)
        )

        val money = when (vipType) {
            "VIP" -> 50000
            "VIPPlus" -> 75000
            "MVIP" -> 125000
            else -> 0
        }

        Database.addMoney(player.lowercase(), money)

        Server.getInstance().broadcastMessage(
            "§7*\n* §2$player §aadlı oyuncunun §2$vipType §aüyelik aldı!\n§7*"
        )
    }

    fun updateVIP(player: String, vipType: String, time: Int) {
        collection.updateOne(
            Filters.and(Filters.eq("player", player), Filters.eq("vipType", vipType)),
            Updates.set("time", time)
        )
    }

    fun removeData(player: String) {
        collection.deleteMany(Filters.eq("player", player))
    }

    fun getPlayer(player: String): Map<String, Any>? {
        val doc = collection.find(Filters.eq("player", player)).first() ?: return null
        return mapOf(
            "player" to doc.getString("player"),
            "vipType" to doc.getString("vipType"),
            "time" to doc.getInteger("time")
        )
    }

    fun deleteVIP(player: String, vipType: String) {
        collection.deleteOne(
            Filters.and(Filters.eq("player", player), Filters.eq("vipType", vipType))
        )
    }

    fun getPlayers(): List<Map<String, Any>> {
        return collection.find().map { doc ->
            mapOf(
                "player" to doc.getString("player"),
                "vipType" to doc.getString("vipType"),
                "time" to doc.getInteger("time")
            )
        }.toList()
    }

    fun getPlayerVIPs(player: String): List<Map<String, Any>> {
        return collection.find(Filters.eq("player", player)).map { doc ->
            mapOf(
                "vipType" to doc.getString("vipType"),
                "time" to doc.getInteger("time")
            )
        }.toList()
    }
}
