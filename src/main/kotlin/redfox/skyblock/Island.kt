package redfox.skyblock

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.block.Block
import cn.nukkit.item.Item
import org.bson.Document
import redfox.skyblock.data.IslandDB
import redfox.skyblock.event.custom.IslandLevelChangeEvent
import redfox.skyblock.permission.Permission

object Island {

    const val DEFAULT_XP = 175

    private val xpCache = mutableMapOf<String, Int>()
    private val levelCache = mutableMapOf<String, Int>()
    val blockCountCache = mutableMapOf<String, MutableMap<String, Int>>() // worldName -> blockType -> count
    val MAX_LIMITS = mapOf(
        "cactus" to 100,
        "hopper" to 50
    )

    fun addXP(worldName: String, amount: Int) {
        val currentLevel = getLevel(worldName)
        val requiredXP = getNeedXP(currentLevel)
        val currentXP = xpCache[worldName] ?: getXP(worldName)
        val nowXp = currentXP + amount

        if (nowXp >= requiredXP) {
            val player = Server.getInstance().getPlayerExact(worldName) ?: return
            val event = IslandLevelChangeEvent(player, currentLevel, currentLevel + 1, worldName)
            Server.getInstance().pluginManager.callEvent(event)

            levelCache[worldName] = currentLevel + 1
            xpCache[worldName] = nowXp - requiredXP

            saveToDB(worldName)
        } else {
            xpCache[worldName] = nowXp
        }
    }

    fun removeXP(playerName: String, amount: Int, player: Player) {
        val currentXp = getXP(playerName) - amount
        val currentLevel = getLevel(playerName)

        if (currentXp < 0) {
            if (currentLevel > 1) {
                val newLevel = currentLevel - 1
                val requiredXp = getNeedXP(newLevel)
                setLevel(playerName, newLevel)
                setXP(playerName, requiredXp - 1)
                val event = IslandLevelChangeEvent(player, currentLevel, newLevel, playerName)
                Server.getInstance().pluginManager.callEvent(event)
            } else {
                setXP(playerName, 0)
            }
        } else {
            setXP(playerName, currentXp)
        }
    }


    private fun saveToDB(worldName: String) {
        val xp = xpCache[worldName] ?: getXP(worldName)
        val level = levelCache[worldName] ?: getLevel(worldName)

        IslandDB.updateXP(worldName, xp)
        IslandDB.collection.updateOne(
            com.mongodb.client.model.Filters.eq("player", worldName),
            com.mongodb.client.model.Updates.set("level", level)
        )
    }

    fun getXP(worldName: String): Int {
        return xpCache[worldName] ?: IslandDB.getIsland(worldName)?.getInteger("xp") ?: 0
    }

    fun getLevel(worldName: String): Int {
        return levelCache[worldName] ?: IslandDB.getIsland(worldName)?.getInteger("level") ?: 1
    }

    fun setXP(worldName: String, xp: Int) {
        xpCache[worldName] = xp
    }

    fun setLevel(worldName: String, level: Int) {
        levelCache[worldName] = level
    }

    fun getPercentageToNextLevel(playerName: String): Int {
        val island = IslandDB.getIsland(playerName) ?: return 0
        val xp = island.getInteger("xp", 0)
        val level = island.getInteger("level", 1)
        val need = getNeedXP(level)
        return ((xp.toDouble() / need) * 100).toInt().coerceAtMost(100)
    }

    fun getRemainingXp(playerName: String): Int {
        val island = IslandDB.getIsland(playerName) ?: return 0
        val xp = island.getInteger("xp", 0)
        val level = island.getInteger("level", 1)
        val need = getNeedXP(level)
        return (need - xp).coerceAtLeast(0)
    }

    fun hasIsland(playerName: String): Boolean {
        return IslandDB.getIsland(playerName) != null
    }

    fun isOwner(playerName: String): Boolean {
        val island = IslandDB.getIsland(playerName) ?: return false
        val owner = island.getString("player") ?: return false
        return owner == playerName
    }

    fun isPartner(playerName: String): Boolean {
        val allIslands = IslandDB.collection.find()
        for (island in allIslands) {
            val partners = island.get("partners", List::class.java) as? List<String> ?: continue
            if (partners.contains(playerName)) {
                return true
            }
        }
        return false
    }

    fun getNeedXP(level: Int): Int {
        return DEFAULT_XP * level
    }

    fun getColorLevel(level: Int = 0): String {
        return when {
            level in 15..29 -> "§9${level} Sv.§r"
            level in 30..50 -> "§e${level} Sv.§r"
            level in 51..70 -> "§a${level} Sv.§r"
            level in 71..100 -> "§c${level} Sv.§r"
            level >= 101 -> "§b${level} Sv.§r"
            else -> "§7${level} Sv.§r"
        }
    }

    fun isPartner(playerName: String, islandName: String): Boolean {
        val island = IslandDB.getIsland(islandName) ?: return false
        val partners = island.getList("partners", String::class.java) ?: return false
        return partners.contains(playerName)
    }

    fun getIslandPartners(islandName: String): List<String>? {
        val island = IslandDB.getIsland(islandName) ?: return null
        return island.getList("partners", String::class.java)
    }

    fun hasPermission(playerName: String, permission: String, islandName: String): Boolean {
        val island = IslandDB.getIsland(islandName) ?: return false
        val permsDoc = island.get("permeds", Document::class.java) ?: return false
        val isPerms = permsDoc.entries.associate { it.key to it.value as? Map<String, Boolean> }

        val playerPerms = isPerms[playerName] ?: return false
        return playerPerms[permission] == true
    }

    fun getWarpCount(islandName: String): Int {
        val island = IslandDB.getIsland(islandName) ?: return 0
        val warpsDoc = island.get("warps", Document::class.java) ?: return 0
        return warpsDoc.size
    }

    fun getMaxWarpLimit(player: Player): Int {
        return when {
            player.server.isOp(player.name) -> 100
            player.hasPermission(Permission.MVIP) -> 8
            player.hasPermission(Permission.VIP_PLUS) -> 6
            player.hasPermission(Permission.VIP) -> 4
            else -> 2
        }
    }

    fun setIslandSpawn(player: Player) {
        val world = Server.getInstance().getLevelByName(player.name) ?: return
        val pos = player.position
        world.setSpawnLocation(pos)
    }

    fun getPercentageToNextLevel(progress: Int, required: Int): String {
        val percentage = (progress.toDouble() / required.toDouble()) * 100
        val formatted = percentage.toInt().coerceAtMost(100)
        return formatted.toString()
    }

    fun getRemainingXp(xp: Int, required: Int): Int {
        return (required - xp).coerceAtLeast(0)
    }

    private val bannedBlocks = setOf(
        Block.COBBLESTONE
    )

    fun getBannedBlocks(): Set<String> {
        return bannedBlocks
    }

    fun getBlockCount(worldName: String, blockType: String): Int {
        val worldCache = blockCountCache[worldName]
        if (worldCache != null && worldCache.containsKey(blockType)) {
            return worldCache[blockType] ?: 0
        }

        val islandDoc = IslandDB.getIsland(worldName)
        val limitsDoc = islandDoc?.get("limits", Document::class.java)

        val count = limitsDoc?.getInteger(blockType) ?: 0

        val cacheMap = blockCountCache.getOrPut(worldName) { mutableMapOf() }
        cacheMap[blockType] = count

        return count
    }


    fun canPlaceBlock(worldName: String, blockType: String): Boolean {
        val island = IslandDB.getIsland(worldName) ?: return false
        val limitDoc = island.get("limits", Document::class.java) ?: return true
        val maxLimit = limitDoc.getInteger(blockType, -1)
        if (maxLimit == -1) return true
        return getBlockCount(worldName, blockType) < maxLimit
    }

    fun incrementBlockCount(worldName: String, blockType: String) {
        val counts = blockCountCache.getOrPut(worldName) { mutableMapOf() }
        counts[blockType] = (counts[blockType] ?: 0) + 1
    }

    fun decrementBlockCount(worldName: String, blockType: String) {
        val counts = blockCountCache[worldName] ?: return
        counts[blockType] = (counts[blockType]?.minus(1) ?: 0).coerceAtLeast(0)
    }

}
