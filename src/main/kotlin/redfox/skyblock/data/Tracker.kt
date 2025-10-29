package redfox.skyblock.data

import cn.nukkit.Player
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import org.bson.Document
import java.util.logging.Logger

object Tracker {

    init {
        Configurator.setLevel("org.mongodb.driver", Level.OFF)
    }

    val collection = MongoDB.db.getCollection("playerTracker")
    private val logger = Logger.getLogger("Tracker")

    fun trackPlayer(player: Player) {
        val data = player.loginChainData

        val uuid = player.uniqueId.toString()
        val name = player.name.lowercase()
        val ip = player.address ?: "Unknown"
        val deviceId = data.deviceId ?: "Unknown"
        val xuid = data.xuid ?: "Unknown"
        val deviceModel = data.deviceModel ?: "Unknown"
        val os = when (data.deviceOS) {
            1 -> "Android"
            2 -> "iOS"
            3 -> "macOS"
            4 -> "FireOS"
            5 -> "GearVR"
            6 -> "Hololens"
            7 -> "Win10"
            8 -> "Win32"
            9 -> "Dedicated"
            10 -> "TVOS"
            11 -> "PlayStation"
            12 -> "Switch"
            13 -> "Xbox"
            14 -> "WindowsPhone"
            else -> "Unknown"
        }
        val language = data.languageCode ?: "Unknown"
        val uiProfile = when (data.uiProfile) {
            0 -> "Classic"
            1 -> "Pocket"
            else -> "Unknown"
        }
        val inputMode = when (data.currentInputMode) {
            0 -> "Touch"
            1 -> "Controller"
            2 -> "Keyboard"
            3 -> "Unknown"
            else -> "Unknown"
        }
        val version = data.gameVersion ?: "Unknown"
        val clientId = data.clientId
        val now = System.currentTimeMillis()

        val playerDoc = Document("uuid", uuid)
            .append("name", name)
            .append("ip", ip)
            .append("deviceId", deviceId)
            .append("xuid", xuid)
            .append("deviceModel", deviceModel)
            .append("operatingSystem", os)
            .append("language", language)
            .append("uiProfile", uiProfile)
            .append("inputMode", inputMode)
            .append("gameVersion", version)
            .append("clientId", clientId)
            .append("lastJoin", now)

        collection.updateOne(
            Document("uuid", uuid),
            Document("\$set", playerDoc),
            UpdateOptions().upsert(true)
        )

        val altAccounts = collection.find(
            Document(
                "\$or", listOf(
                    Document("ip", ip),
                    Document("deviceId", deviceId)
                )
            )
        ).toList()

        if (altAccounts.size > 1) {
            val altNames = altAccounts.map { it.getString("name") }.distinct()
            if (altNames.size > 1) {
                logger.info("[Tracker] Aynı IP veya DeviceId kullanan farklı oyuncular tespit edildi: $altNames")
            }
        }
    }


    fun getUUIDByName(name: String): String? {
        val doc = collection.find(Filters.eq("name", name.lowercase())).firstOrNull() ?: return null
        return doc.getString("uuid")
    }
}
