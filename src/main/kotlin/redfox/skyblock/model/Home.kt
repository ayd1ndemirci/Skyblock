package redfox.skyblock.model

import org.bson.Document

data class Home(
    val name: String,
    val world: String,
    val x: Double,
    val y: Double,
    val z: Double
) {
    fun toDocument(): Document {
        return Document("world", world)
            .append("x", x)
            .append("y", y)
            .append("z", z)
    }

    companion object {
        fun fromDocument(name: String, doc: Document): Home {
            return Home(
                name = name,
                world = doc.getString("world"),
                x = doc.getDouble("x"),
                y = doc.getDouble("y"),
                z = doc.getDouble("z")
            )
        }
    }
}
