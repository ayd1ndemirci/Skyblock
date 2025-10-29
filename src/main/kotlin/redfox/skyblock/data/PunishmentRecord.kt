package redfox.skyblock.data

import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import org.bson.Document
import redfox.skyblock.data.MongoDB.db
import redfox.skyblock.enums.PunishmentType
import java.text.SimpleDateFormat
import java.util.*

object PunishmentRecord {

    init {
        Configurator.setLevel("org.mongodb.driver", Level.OFF)
    }

    private val collection = db.getCollection("punishment_records")
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    private fun nowString(): String = dateFormat.format(Date())

    private fun formatTimestamp(millis: Long): String = dateFormat.format(Date(millis))

    fun addPunishment(playerName: String, reason: String, issuedBy: String, until: Long, type: PunishmentType) {
        val nameLower = playerName.lowercase()
        val now = System.currentTimeMillis()
        val punishment = Document()
            .append("type", type.name)
            .append("reason", reason)
            .append("issuedBy", issuedBy)
            .append("created", nowString())

        if (type == PunishmentType.BAN || type == PunishmentType.MUTE) {
            punishment.append("until", formatTimestamp(until))
        }

        val filter = Filters.eq("name", nameLower)
        val update = Updates.combine(
            Updates.setOnInsert("_id", nameLower),
            Updates.setOnInsert("name", nameLower),
            Updates.push("history", punishment)
        )
        collection.updateOne(filter, update, UpdateOptions().upsert(true))
    }

    fun isPunished(playerName: String, type: PunishmentType): Boolean {
        val history = getHistory(playerName)
        val now = System.currentTimeMillis()
        return history.any {
            it.getString("type") == type.name && (parseDate(it.getString("until"))?.time ?: 0) > now
        }
    }

    fun getRemaining(playerName: String, type: PunishmentType): Long {
        val now = System.currentTimeMillis()
        return getHistory(playerName)
            .firstOrNull {
                it.getString("type") == type.name && (parseDate(it.getString("until"))?.time ?: 0) > now
            }?.let {
                parseDate(it.getString("until"))?.time?.minus(now)
            } ?: 0L
    }

    fun getEnd(playerName: String, type: PunishmentType): String {
        return getHistory(playerName)
            .firstOrNull {
                it.getString("type") == type.name && (parseDate(it.getString("until"))?.time
                    ?: 0) > System.currentTimeMillis()
            }?.getString("until") ?: "Süresiz veya geçmiş"
    }

    fun getReason(playerName: String, type: PunishmentType): String {
        return getHistory(playerName)
            .firstOrNull {
                it.getString("type") == type.name && (parseDate(it.getString("until"))?.time
                    ?: 0) > System.currentTimeMillis()
            }?.getString("reason") ?: "Bilinmeyen"
    }
    fun getHistory(playerName: String): List<Document> {
        val nameLower = playerName.lowercase()
        val doc = collection.find(Filters.eq("name", nameLower)).firstOrNull() ?: return emptyList()
        return doc.getList("history", Document::class.java) ?: emptyList()
    }

    private fun parseDate(dateString: String?): Date? {
        return try {
            if (dateString != null) dateFormat.parse(dateString) else null
        } catch (e: Exception) {
            null
        }
    }
}
