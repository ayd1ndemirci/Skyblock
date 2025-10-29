package redfox.skyblock.data

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import org.bson.Document
import redfox.skyblock.data.MongoDB.db
import com.mongodb.client.model.Sorts


object Auction {

    init {
        Configurator.setLevel("org.mongodb.driver", Level.OFF)
    }

    private val collection = db.getCollection("auctions")

    fun createAuction(player: String, price: Int, instantPrice: Int, itemBase64: String) {
        val nextId = getNextId()
        val doc = Document()
            .append("_id", nextId)
            .append("player", player)
            .append("price", price)
            .append("instantPrice", instantPrice)
            .append("item", itemBase64)
            .append("createdAt", System.currentTimeMillis())
            .append("lastBidder", null)
        collection.insertOne(doc)
    }

    private fun getNextId(): Int {
        val last = collection.find()
            .sort(Sorts.descending("_id"))
            .limit(1)
            .firstOrNull()
        return (last?.getInteger("_id") ?: -1) + 1
    }

    fun getAuction(id: Int): Document? {
        return collection.find(Filters.eq("_id", id)).firstOrNull()
    }

    fun setLastBidder(id: Int, player: String) {
        collection.updateOne(
            Filters.eq("_id", id),
            Updates.set("lastBidder", player)
        )
    }

    fun deleteAuction(id: Int) {
        collection.deleteOne(Filters.eq("_id", id))
    }

    fun getAll(): List<Document> {
        return collection.find().toList()
    }

    fun clearExpiredAuctions(timeoutMs: Long) {
        val now = System.currentTimeMillis()
        val iterator = collection.find().iterator()
        while (iterator.hasNext()) {
            val doc = iterator.next()
            val createdAt = doc.getLong("createdAt") ?: continue
            if ((now - createdAt) > timeoutMs) {
                collection.deleteOne(Filters.eq("_id", doc.getInteger("_id")))
            }
        }
    }
}
