package redfox.skyblock.data

import cn.nukkit.math.Vector3
import com.google.gson.Gson
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.client.model.Updates.*
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import org.bson.Document
import redfox.skyblock.data.MongoDB.db

object IslandDB {

    init {
        Configurator.setLevel("org.mongodb.driver", Level.OFF)
    }

    val collection: MongoCollection<Document> = db.getCollection("islands")
    private val gson = Gson()

    fun createIsland(playerName: String) {
        val canCreate = canCreateIsland(playerName)
        if (!canCreate) return
        val exists = collection.find(eq("player", playerName)).firstOrNull()
        if (exists != null) return

        val doc = Document()
            .append("player", playerName)
            .append("xp", 0)
            .append("level", 1)
            .append("partners", mutableListOf<String>())
            .append("members", Document())
            .append("settings", Document())
            .append("visit", Document())
            .append("spawnLocation", Document(mapOf("x" to 0, "y" to 64, "z" to 0)))
            .append("warps", Document())
            .append("blockeds", Document())
            .append("permeds", Document())
            .append("advertisement", 0L)
            .append("deleteTime", 0L)
            .append("limits", Document(mapOf(
                "cactus" to 0,
                "hopper" to 0
            )))

        collection.insertOne(doc)
    }

    fun getIsland(playerName: String?): Document? {
        return collection.find(eq("player", playerName)).firstOrNull()
    }

    fun updateXP(playerName: String, xp: Int) {
        collection.updateOne(eq("player", playerName), set("xp", xp))
    }

    fun setXP(playerName: String, xp: Int) {
        updateXP(playerName, xp)
    }

    fun setLevel(playerName: String, level: Int) {
        collection.updateOne(eq("player", playerName), set("level", level))
    }

    fun addPartner(playerName: String, partnerName: String) {
        collection.updateOne(eq("player", playerName), addToSet("partners", partnerName))
    }

    fun removePartner(playerName: String, partnerName: String) {
        collection.updateOne(eq("player", playerName), pull("partners", partnerName))
    }

    fun updateSpawnLocation(playerName: String, x: Double, y: Double, z: Double) {
        val spawnDoc = Document(mapOf("x" to x, "y" to y, "z" to z))
        collection.updateOne(eq("player", playerName), set("spawnLocation", spawnDoc))
    }

    fun setMemberPermissions(playerName: String, memberName: String, permissions: List<String>) {
        val key = "members.$memberName"
        collection.updateOne(eq("player", playerName), set(key, permissions))
    }

    fun getMemberPermissions(playerName: String, memberName: String): List<String>? {
        val island = getIsland(playerName) ?: return null
        val members = island.get("members", Document::class.java) ?: return null
        return members.getList(memberName, String::class.java)
    }

    fun getPlayerPartner(playerName: String?): String? {
        if (getIsland(playerName) != null) return playerName

        val allIslands = collection.find()
        for (island in allIslands) {
            val partners = island.getList("partners", String::class.java) ?: continue
            if (partners.contains(playerName)) {
                return island.getString("player")
            }
        }
        return null
    }

    fun getIslands(): List<Document> {
        return collection.find().into(mutableListOf())
    }

    fun getSettings(playerName: String): Document {
        val island = getIsland(playerName) ?: return Document()
        return island.get("settings", Document::class.java) ?: Document()
    }

    fun updatePlayerIslandSettings(playerName: String, settings: Document) {
        collection.updateOne(eq("player", playerName), set("settings", settings))
    }

    fun updateBlockedsPlayer(playerName: String, blockeds: List<String>) {
        collection.updateOne(eq("player", playerName), set("blockeds", blockeds))
    }

    fun getWarps(playerName: String?): List<String> {
        val island = getIsland(playerName) ?: return emptyList()
        val warpsDoc = island.get("warps", Document::class.java) ?: return emptyList()
        return warpsDoc.keys.toList()
    }

    fun getWarpPosition(warpName: String, playerName: String): Vector3? {
        val island = getIsland(playerName) ?: return null
        val warpsDoc = island.get("warps", Document::class.java) ?: return null
        val posJson = warpsDoc.getString(warpName) ?: return null

        return try {
            gson.fromJson(posJson, Vector3::class.java)
        } catch (e: Exception) {
            val parts = posJson.split(":")
            if (parts.size == 3) {
                val x = parts[0].toDoubleOrNull() ?: return null
                val y = parts[1].toDoubleOrNull() ?: return null
                val z = parts[2].toDoubleOrNull() ?: return null
                Vector3(x, y, z)
            } else null
        }
    }

    fun addOrUpdateWarp(playerName: String, warpName: String, position: Vector3) {
        val island = getIsland(playerName) ?: return
        val warpsDoc = island.get("warps", Document::class.java) ?: Document()
        val warpsMap: MutableMap<String, String> = warpsDoc.entries.associate {
            it.key to it.value.toString()
        }.toMutableMap()

        warpsMap[warpName] = gson.toJson(position)

        // Document'a çevir ve DB'ye yaz
        val newWarpsDoc = Document()
        warpsMap.forEach { (k, v) -> newWarpsDoc.append(k, v) }

        collection.updateOne(eq("player", playerName), set("warps", newWarpsDoc))
    }

    fun removeWarp(playerName: String, warpName: String) {
        val island = getIsland(playerName) ?: return
        val warpsDoc = island.get("warps", Document::class.java) ?: return
        val warpsMap: MutableMap<String, String> = warpsDoc.entries.associate {
            it.key to it.value.toString()
        }.toMutableMap()

        warpsMap.remove(warpName)

        val newWarpsDoc = Document()
        warpsMap.forEach { (k, v) -> newWarpsDoc.append(k, v) }

        collection.updateOne(eq("player", playerName), set("warps", newWarpsDoc))
    }

    fun getWarpCount(playerName: String): Int {
        val island = getIsland(playerName) ?: return 0
        val warpsDoc = island.get("warps", Document::class.java) ?: return 0
        return warpsDoc.size
    }

    fun updateDeleteTime(playerName: String, timestamp: Long) {
        collection.updateOne(eq("player", playerName), set("deleteTime", timestamp))
    }

    fun getDeleteTime(playerName: String): Long? {
        val island = getIsland(playerName) ?: return null
        return island.getLong("deleteTime")
    }

    fun canCreateIsland(playerName: String): Boolean {
        val deleteTime = getDeleteTime(playerName) ?: return true
        val currentTime = System.currentTimeMillis() / 1000
        return currentTime >= deleteTime
    }

    fun removeIsland(playerName: String) {
        collection.deleteOne(eq("player", playerName))
    }

    fun updatePlayerPartners(playerName: String, partners: List<String>) {
        collection.updateOne(eq("player", playerName), set("partners", partners))
    }

    fun updatePermedsPlayer(playerName: String, permeds: String) {
        collection.updateOne(eq("player", playerName), set("permeds", permeds))
    }

    fun save(owner: String, doc: Document) {
        collection.replaceOne(eq("player", owner), doc, ReplaceOptions().upsert(true))
    }

    fun saveBlockCounts(worldName: String, blockCounts: Map<String, Int>) {
        val limitsDoc = Document()
        for ((blockType, count) in blockCounts) {
            limitsDoc.append(blockType, count)
        }
        collection.updateOne(
            eq("player", worldName),
            set("limits", limitsDoc)
        )
    }

}
