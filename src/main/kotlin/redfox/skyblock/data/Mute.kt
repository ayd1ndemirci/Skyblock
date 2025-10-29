package redfox.skyblock.data

import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import redfox.skyblock.data.MongoDB.db

object Mute {

    init {
        Configurator.setLevel("org.mongodb.driver", Level.OFF)
    }

    private val collection = db.getCollection("mutes")

    fun isMuted(playerName: String): Boolean {
        val doc = collection.find(Filters.eq("name", playerName)).firstOrNull() ?: return false
        val until = doc.getLong("muteUntil")
        return until > System.currentTimeMillis()
    }

    fun getRemaining(playerName: String): Long {
        val doc = collection.find(Filters.eq("name", playerName)).firstOrNull() ?: return 0L
        val until = doc.getLong("muteUntil")
        return until - System.currentTimeMillis()
    }

    fun setMute(playerName: String, reason: String, mutedBy: String, muteUntil: Long) {
        val filter = Filters.eq("name", playerName)
        val update = Updates.combine(
            Updates.set("name", playerName),
            Updates.set("reason", reason),
            Updates.set("mutedBy", mutedBy),
            Updates.set("muteUntil", muteUntil),
            Updates.set("createdAt", System.currentTimeMillis())
        )
        collection.updateOne(filter, update, UpdateOptions().upsert(true))
    }

    fun unmute(playerName: String) {
        collection.deleteOne(Filters.eq("name", playerName))
    }

    fun getReason(playerName: String): String {
        val doc = collection.find(Filters.eq("name", playerName)).firstOrNull() ?: return "Bilinmeyen"
        return doc.getString("reason") ?: "Bilinmeyen"
    }

    fun getEnd(playerName: String): Long {
        val doc = collection.find(Filters.eq("name", playerName)).firstOrNull() ?: return 0L
        return doc.getLong("muteUntil") ?: 0L
    }

    fun clearExpiredMutes() {
        val now = System.currentTimeMillis()
        val iterator = collection.find().iterator()
        while (iterator.hasNext()) {
            val doc = iterator.next()
            val muteUntil = doc.getLong("muteUntil")
            if (muteUntil <= now) {
                val name = doc.getString("name")
                collection.deleteOne(Filters.eq("name", name))
            }
        }
    }
}