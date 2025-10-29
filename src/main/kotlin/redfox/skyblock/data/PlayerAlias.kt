package redfox.skyblock.data

import cn.nukkit.Player
import com.mongodb.client.model.Filters
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import org.bson.Document
import redfox.skyblock.data.MongoDB.db

object PlayerAlias {

    init {
        Configurator.setLevel("org.mongodb.driver", Level.OFF)
    }


    private val data = db.getCollection("player_unique_data")

    fun getData(name: String): Document? {
        return data.find(Filters.eq("name", name)).first()
    }

    fun setData(name: String, xuid: String, uniqueId: String, deviceId: String, ip: String, clientId: Long) {
        val existingData = getData(name)
        if (existingData != null) {
            data.updateOne(
                Filters.eq("name", name),
                Document(
                    "\$set", Document("xuid", xuid)
                        .append("uniqueId", uniqueId)
                        .append("deviceId", deviceId)
                        .append("ip", ip)
                        .append("clientId", clientId)
                )
            )
        } else {
            val doc = Document("name", name)
                .append("xuid", xuid)
                .append("uniqueId", uniqueId)
                .append("deviceId", deviceId)
                .append("ip", ip)
                .append("clientId", clientId)
            data.insertOne(doc)
        }
    }

    fun getAliases(name: String): List<String> {
        val playerData = getData(name)
        if (playerData == null) return listOf()
        return data.find(
            Filters.or(
                Filters.eq("name", playerData.getString("name")),
                Filters.eq("xuid", playerData.getString("xuid")),
                Filters.eq("uniqueId", playerData.getString("uniqueId")),
                Filters.eq("deviceId", playerData.getString("deviceId")),
                Filters.eq("ip", playerData.getString("ip")),
                Filters.eq("clientId", playerData.getLong("clientId"))
            )
        ).toList()
            .mapNotNull { it.getString("name") }
            .distinct()
            .filter { it != name }
            .sortedBy { it.lowercase() }
    }

    fun getAliases(player: Player): List<String> {
        return getAliases(player.name)
    }
}