package redfox.skyblock.manager

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import org.bson.Document
import redfox.skyblock.data.MongoDB

object WebManager {

    private val users = MongoDB.db.getCollection("users")

    enum class PurchaseType {
        CREDIT, VIP, ITEM, SPECIAL
    }

    fun addPurchase(
        username: String,
        itemName: String,
        type: PurchaseType,
        amount: Int = 1,
        timestamp: Long = System.currentTimeMillis()
    ): Boolean {
        val purchase = Document("item", itemName)
            .append("type", type.name.lowercase())
            .append("amount", amount)
            .append("timestamp", timestamp)

        val result = users.updateOne(
            Filters.eq("username", username),
            Updates.push("purchase_history", purchase)
        )

        return result.modifiedCount > 0
    }

    fun getPurchases(username: String): List<Document> {
        val userDoc = users.find(Filters.eq("username", username)).first() ?: return emptyList()
        return userDoc.getList("purchase_history", Document::class.java) ?: emptyList()
    }

    fun removePurchase(username: String, itemName: String, type: PurchaseType): Boolean {
        val filter = Document("item", itemName).append("type", type.name.lowercase())
        val result = users.updateOne(
            Filters.eq("username", username),
            Updates.pull("purchase_history", filter)
        )
        return result.modifiedCount > 0
    }

    fun clearPurchases(username: String): Boolean {
        val result = users.updateOne(
            Filters.eq("username", username),
            Updates.set("purchase_history", listOf<Document>())
        )
        return result.modifiedCount > 0
    }
}
