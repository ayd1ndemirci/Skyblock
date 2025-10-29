package redfox.skyblock.data

import com.mongodb.client.MongoCollection
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import org.bson.Document

class Web {

    init {
        Configurator.setLevel("org.mongodb.driver", Level.OFF)
    }

    private val users: MongoCollection<Document> = MongoDB.db.getCollection("users")

    fun getPlayer(name: String): Document? {
        return users.find(Document("username", name)).first()
    }

    fun addPlayer(
        name: String,
        password: String,
        role: String,
        credit: Int,
        isBanned: Boolean,
        createdAt: Long,
        updatedAt: Long
    ) {
        val doc = Document("username", name)
            .append("password", password)
            .append("role", role)
            .append("credit", credit)
            .append("isBanned", isBanned)
            .append("created_at", createdAt)
            .append("updated_at", updatedAt)
            .append("purchase_history", listOf<Document>())

        users.insertOne(doc)
    }


    fun updatePlayer(name: String, updates: Map<String, Any>): Boolean {
        val updateResult = users.updateOne(
            Document("username", name),
            Document("\$set", Document(updates))
        )
        return updateResult.modifiedCount > 0
    }

    fun removePlayer(name: String): Boolean {
        return users.deleteOne(Document("username", name)).deletedCount > 0
    }
}
