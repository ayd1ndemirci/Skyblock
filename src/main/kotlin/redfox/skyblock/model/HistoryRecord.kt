package redfox.skyblock.model

import org.bson.Document
import java.time.Instant

data class HistoryRecord(
    val name: String,
    val action: String,
    val amount: Int,
    val timestamp: Long = Instant.now().epochSecond
) {
    fun toDocument(): Document {
        return Document()
            .append("name", name)
            .append("action", action)
            .append("amount", amount)
            .append("timestamp", timestamp)
    }

    companion object {
        fun fromDocument(doc: Document): HistoryRecord {
            return HistoryRecord(
                name = doc.getString("name"),
                action = doc.getString("action"),
                amount = doc.getInteger("amount"),
                timestamp = doc.getLong("timestamp")
            )
        }
    }
}