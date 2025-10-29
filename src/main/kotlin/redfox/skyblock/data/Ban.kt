package redfox.skyblock.data

import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import org.apache.logging.log4j.core.config.Configurator
import org.apache.logging.log4j.Level
import redfox.skyblock.data.MongoDB.db

object Ban {

    init {
        Configurator.setLevel("org.mongodb.driver", Level.OFF)
    }

    private val collection = db.getCollection("bans")

    fun isBanned(playerName: String): Boolean {
        val doc = collection.find(Filters.eq("name", playerName)).firstOrNull() ?: return false
        val until = doc.getLong("banUntil")
        return until > System.currentTimeMillis()
    }

    fun getRemaining(playerName: String): Long {
        val doc = collection.find(Filters.eq("name", playerName)).firstOrNull() ?: return 0L
        val until = doc.getLong("banUntil")
        return until - System.currentTimeMillis()
    }

    fun setBan(playerName: String, reason: String, bannedBy: String, banUntil: Long) {
        val filter = Filters.eq("name", playerName)
        val update = Updates.combine(
            Updates.set("name", playerName),
            Updates.set("reason", reason),
            Updates.set("bannedBy", bannedBy),
            Updates.set("banUntil", banUntil),
            Updates.set("createdAt", System.currentTimeMillis())
        )
        collection.updateOne(filter, update, UpdateOptions().upsert(true))
    }

    fun unban(playerName: String) {
        collection.deleteOne(Filters.eq("name", playerName))
    }

    fun getReason(playerName: String): String {
        val doc = collection.find(Filters.eq("name", playerName)).firstOrNull() ?: return "Bilinmeyen"
        return doc.getString("reason") ?: "Bilinmeyen"
    }

    fun getEnd(playerName: String): Long {
        val doc = collection.find(Filters.eq("name", playerName)).firstOrNull() ?: return 0L
        return doc.getLong("banUntil") ?: 0L
    }

    fun clearExpiredBans() {
        val now = System.currentTimeMillis()
        val iterator = collection.find().iterator()
        while (iterator.hasNext()) {
            val doc = iterator.next()
            val until = doc.getLong("banUntil") ?: continue
            if (until <= now) {
                val name = doc.getString("name") ?: continue
                collection.deleteOne(Filters.eq("name", name))
            }
        }
    }
}