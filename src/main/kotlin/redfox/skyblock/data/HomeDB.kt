package redfox.skyblock.data

import cn.nukkit.Player
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import org.bson.Document
import redfox.skyblock.model.Home

object HomeDB {

    init {
        Configurator.setLevel("org.mongodb.driver", Level.OFF)
    }


    private val collection = MongoDB.db.getCollection("homes")

    fun getHomes(player: Player): Map<String, Home> {
        val doc = collection.find(Filters.eq("name", player.name)).first() ?: return emptyMap()
        val homeDoc = doc.get("homes", Document::class.java) ?: return emptyMap()

        return homeDoc.entries.associate { (key, value) ->
            key to Home.fromDocument(key, value as Document)
        }
    }

    fun setHome(player: Player, homeName: String) {
        val pos = player.location
        setHome(player, homeName, Home(homeName, pos.level.name, pos.x, pos.y, pos.z))
    }

    fun setHome(player: Player, homeName: String, home: Home) {
        val filter = Filters.eq("name", player.name)
        val update = Updates.set("homes.$homeName", home.toDocument())
        collection.updateOne(filter, update, UpdateOptions().upsert(true))
    }

    fun deleteHome(player: Player, homeName: String) {
        val filter = Filters.eq("name", player.name)
        val update = Updates.unset("homes.$homeName")
        collection.updateOne(filter, update)
    }

    fun createEmptyHomesDocument(playerName: String) {
        val collection = MongoDB.db.getCollection("homes")
        val doc = Document("name", playerName)
            .append("homes", Document())
        collection.insertOne(doc)
    }

}