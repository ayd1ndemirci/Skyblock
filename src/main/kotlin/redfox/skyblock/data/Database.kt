package redfox.skyblock.data

import cn.nukkit.Player
import cn.nukkit.command.CommandSender
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import org.bson.Document
import redfox.skyblock.Core
import redfox.skyblock.data.MongoDB.db
import redfox.skyblock.model.HistoryRecord
import redfox.skyblock.utils.Utils
import redfox.skyblock.utils.Utils.DEFAULT_PAGE_LIMIT
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object Database {

    init {
        Configurator.setLevel("org.mongodb.driver", Level.OFF)
    }


    private val collection = db.getCollection("players")

    fun createIfNotExist(name: String) {
        val now = LocalDateTime.now()
        val unixTime = now.toEpochSecond(ZoneOffset.UTC)
        val formatted = now.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
        if (collection.find(Filters.eq("name", name)).first() == null) {
            val doc = Document("name", name)
                .append("money", Utils.DEFAULT_MONEY)
                .append("credi", 0)
                .append("crediHistory", mutableListOf<Document>())
                .append("kills", 0)
                .append("deaths", 0)
                .append("friends", mutableListOf<String>())
                .append("incomingRequests", mutableListOf<String>())
                .append("outgoingRequests", mutableListOf<String>())
                .append("joinMessage", null)
                .append("kits", listOf<Document>())
                .append("playTime", 0)
                .append("rank", null)
                .append("rankExpire", null)
                .append("settings", getDefaultSettings())
                .append("shoutRight", 0)
                .append("shoutBanned", false)
                .append("firstJoinUnix", unixTime)
                .append("firstJoinFormatted", formatted)
                .append("lastJoin", formatted)
                .append("loginStreak", 1)
                .append("lastAnniversaryReward", 0L)
                .append("badges", listOf<String>())

            collection.insertOne(doc)
        }
    }

    fun get(name: String): Document? {
        return collection.find(Filters.eq("name", name.lowercase())).firstOrNull()
    }

    fun set(name: String, key: String, value: Any) {
        collection.updateOne(
            Filters.eq("name", name.lowercase()),
            Updates.set(key, value),
            UpdateOptions().upsert(true)
        )
    }

    fun inc(name: String, key: String, value: Int) {
        collection.updateOne(
            Filters.eq("name", name.lowercase()),
            Updates.inc(key, value),
            UpdateOptions().upsert(true)
        )
    }

    fun pushToList(name: String, key: String, value: Any) {
        collection.updateOne(
            Filters.eq("name", name.lowercase()),
            Updates.push(key, value),
            UpdateOptions().upsert(true)
        )
    }

    fun pullFromList(name: String, key: String, value: Any) {
        collection.updateOne(
            Filters.eq("name", name.lowercase()),
            Updates.pull(key, value),
            UpdateOptions().upsert(true)
        )
    }

    fun getDefaultSettings(): Document {
        return Document()
            .append("message", true)
            .append("tpa", true)
            .append("gift", true)
            .append("friend", true)
            .append("notices", true)
            .append("autoinv", true)
            .append("durability", true)
    }

    fun getMoney(name: String): Int {
        val doc = get(name.lowercase()) ?: return 0
        val value = doc.get("money")
        return when (value) {
            is Int -> value
            is Long -> value.toInt()
            is Double -> value.toInt()
            else -> 0
        }
    }

    fun setMoney(name: String, value: Int) {
        if (value < 0) {
            Core.instance.logger.warning("MONEY set negatif değer reddedildi: ${name.lowercase()}")
            return
        }
        Core.instance.logger.info("MONEY set $value, ${name.lowercase()}")
        set(name.lowercase(), "money", value)
    }

    fun addMoney(name: String, amount: Int) {
        if (amount <= 0) {
            Core.instance.logger.warning("MONEY add negatif veya sıfır değer reddedildi: $amount, ${name.lowercase()}")
            return
        }
        Core.instance.logger.info("MONEY add $amount, ${name.lowercase()}")
        inc(name.lowercase(), "money", amount)
    }

    fun removeMoney(name: String, amount: Int) {
        if (amount <= 0) {
            Core.instance.logger.warning("MONEY remove negatif veya sıfır değer reddedildi: $amount, ${name.lowercase()}")
            return
        }

        val current = getMoney(name)
        if (current < amount) {
            Core.instance.logger.warning("MONEY remove reddedildi, yetersiz bakiye: $amount, ${name.lowercase()} (bakiye: $current)")
            return
        }

        Core.instance.logger.info("MONEY remove $amount, ${name.lowercase()}")
        inc(name.lowercase(), "money", -amount)
    }

    fun hasMoney(name: String, amount: Int): Boolean {
        if (amount <= 0) return true
        return getMoney(name.lowercase()) >= amount
    }

    fun getMoney(p: CommandSender): Int {
        if (p !is Player) return 0
        return getMoney(p.name.lowercase())
    }

    fun setMoney(p: CommandSender, value: Int) {
        if (p !is Player) return
        setMoney(p.name.lowercase(), value)
    }

    fun addMoney(p: CommandSender, amount: Int) {
        if (p !is Player) return
        addMoney(p.name.lowercase(), amount)
    }

    fun removeMoney(p: CommandSender, amount: Int) {
        if (p !is Player) return
        removeMoney(p.name.lowercase(), amount)
    }

    fun hasMoney(p: CommandSender, amount: Int): Boolean {
        if (p !is Player) return false
        return hasMoney(p.name.lowercase(), amount)
    }

    fun topMoney(page: Int = 0, limit: Int = DEFAULT_PAGE_LIMIT): List<Pair<String, Int>> {
        return collection.find()
            .sort(Document("money", -1))
            .skip(page * limit)
            .limit(limit)
            .map {
                val name = it.getString("name") ?: "Unknown"
                val value = when (val v = it.get("money")) {
                    is Int -> v
                    is Long -> v.toInt()
                    is Double -> v.toInt()
                    else -> 0
                }
                name to value
            }
            .toList()
    }

    fun moneyPageCount(limit: Int = DEFAULT_PAGE_LIMIT): Int {
        val count = collection.countDocuments()
        return ((count + limit - 1) / limit).toInt()
    }

    fun getMoneyRank(name: String): Int {
        val userMoney = getMoney(name.lowercase())
        if (userMoney <= 0) return -1
        val count = collection.countDocuments(Filters.gt("money", userMoney))
        return count.toInt()
    }

    fun getCredi(name: String): Int {
        return get(name.lowercase())?.getInteger("credi") ?: 0
    }

    fun setCredi(name: String, value: Int) {
        set(name.lowercase(), "credi", value)
    }

    fun addCredi(name: String, amount: Int) {
        inc(name.lowercase(), "credi", amount)
    }

    fun removeCredi(name: String, amount: Int) {
        inc(name.lowercase(), "credi", -amount)
    }

    fun hasCredi(name: String, amount: Int): Boolean {
        return getCredi(name.lowercase()) >= amount
    }

    fun getCrediTop(page: Int = 0, limit: Int = 10): List<Pair<String, Int>> {
        return collection.find()
            .sort(Document("credi", -1))
            .skip(page * limit)
            .limit(limit)
            .map { Pair(it.getString("name"), it.getInteger("credi", 0)) }
            .toList()
    }

    fun getCrediRank(name: String): Int {
        val userCredi = getCredi(name)
        val count = collection.countDocuments(Filters.gt("credi", userCredi))
        return count.toInt()
    }

    fun addCrediRecord(name: String, record: HistoryRecord) {
        collection.updateOne(
            Filters.eq("name", name.lowercase()),
            Updates.push("crediHistory", record.toDocument()),
            UpdateOptions().upsert(true)
        )
    }

    fun getLastCrediRecords(name: String, limit: Int = 10): List<HistoryRecord> {
        val doc = collection.find(Filters.eq("name", name.lowercase())).firstOrNull() ?: return emptyList()
        val history = doc.getList("crediHistory", Document::class.java) ?: return emptyList()

        return history
            .sortedByDescending {
                val ts = it.get("timestamp")
                when (ts) {
                    is Long -> ts
                    is Int -> ts.toLong()
                    else -> 0L
                }
            }
            .take(limit)
            .map { HistoryRecord.fromDocument(it) }
    }

    fun getFriends(name: String): List<String> {
        val doc = get(name.lowercase()) ?: return emptyList()
        return doc.getList("friends", String::class.java) ?: emptyList()
    }

    fun addFriend(playerName: String, targetName: String) {
        val name = playerName.lowercase()
        val target = targetName.lowercase()

        if (name == target) return

        pushToList(name, "friends", target)
        pushToList(target, "friends", name)

        pullFromList(name, "incomingRequests", target)
        pullFromList(name, "outgoingRequests", target)
        pullFromList(target, "incomingRequests", name)
        pullFromList(target, "outgoingRequests", name)
    }

    fun removeFriend(playerName: String, targetName: String) {
        val name = playerName.lowercase()
        val target = targetName.lowercase()

        pullFromList(name, "friends", target)
        pullFromList(target, "friends", name)
    }

    fun cancelFriendRequest(fromPlayer: String, toPlayer: String) {
        val from = fromPlayer.lowercase()
        val to = toPlayer.lowercase()

        collection.updateOne(
            Filters.eq("name", from),
            Updates.pull("outgoingRequests", to)
        )
        collection.updateOne(
            Filters.eq("name", to),
            Updates.pull("incomingRequests", from)
        )
    }

    fun sendFriendRequest(fromName: String, toName: String) {
        val from = fromName.lowercase()
        val to = toName.lowercase()

        if (from == to) return

        if (getFriends(from).contains(to)) return
        if (getIncomingRequests(to).contains(from)) return
        if (getOutgoingRequests(from).contains(to)) return

        pushToList(from, "outgoingRequests", to)
        pushToList(to, "incomingRequests", from)
    }

    fun acceptFriendRequest(playerName: String, fromName: String) {
        val name = playerName.lowercase()
        val from = fromName.lowercase()

        if (!getIncomingRequests(name).contains(from)) return

        addFriend(name, from)
    }

    fun denyFriendRequest(playerName: String, fromName: String) {
        val name = playerName.lowercase()
        val from = fromName.lowercase()

        pullFromList(name, "incomingRequests", from)
        pullFromList(from, "outgoingRequests", name)
    }

    fun getOutgoingRequests(name: String): List<String> {
        val doc = get(name.lowercase()) ?: return emptyList()
        return doc.getList("outgoingRequests", String::class.java) ?: emptyList()
    }

    fun getIncomingRequests(name: String): List<String> {
        val doc = get(name.lowercase()) ?: return emptyList()
        return doc.getList("incomingRequests", String::class.java) ?: emptyList()
    }

    fun isFriend(name: String, other: String): Boolean {
        return getFriends(name.lowercase()).contains(other.lowercase())
    }

    fun getPlayerKits(name: String): List<Document> {
        val doc = get(name.lowercase()) ?: return emptyList()
        return doc.getList("kits", Document::class.java) ?: emptyList()
    }

    fun hasKit(name: String, kitType: String): Boolean {
        return getPlayerKits(name).any { it.getString("kitType") == kitType }
    }

    fun addOrUpdateKit(name: String, kitType: String, time: Long) {
        val playerName = name.lowercase()
        val doc = get(playerName) ?: return

        val currentKits = doc.getList("kits", Document::class.java)?.toMutableList() ?: mutableListOf()
        val index = currentKits.indexOfFirst { it.getString("kitType") == kitType }

        val newKit = Document("kitType", kitType).append("time", time)

        if (index != -1) {
            currentKits[index] = newKit
        } else {
            currentKits.add(newKit)
        }

        set(playerName, "kits", currentKits)
    }

    fun removeKit(name: String, kitType: String) {
        val playerName = name.lowercase()
        val doc = get(playerName) ?: return

        val currentKits = doc.getList("kits", Document::class.java)?.toMutableList() ?: return
        currentKits.removeIf { it.getString("kitType") == kitType }

        set(playerName, "kits", currentKits)
    }

    fun clearKits(name: String) {
        set(name.lowercase(), "kits", listOf<Document>())
    }

    fun getLastKitTakenTime(name: String, kitType: String): Long? {
        val kits = getPlayerKits(name)
        return kits.firstOrNull { it.getString("kitType") == kitType }?.getLong("time")
    }

    fun addPlayTime(name: String, time: Int) {
        set(name.lowercase(), "playTime", time)
    }

    fun hasPlayTimeData(name: String): Boolean {
        val doc = get(name.lowercase())
        return doc != null && doc.containsKey("playTime")
    }

    fun updatePlayTime(name: String, time: Int) {
        set(name.lowercase(), "playTime", time)
    }

    fun removePlayTime(name: String) {
        collection.updateOne(
            Filters.eq("name", name.lowercase()),
            Updates.unset("playTime")
        )
    }

    fun getPlayTime(name: String): Int {
        val doc = get(name.lowercase()) ?: return 0
        return doc.getInteger("playTime", 0)
    }

    fun getAllPlayTimes(): List<Pair<String, Int>> {
        return collection.find()
            .map {
                val name = it.getString("name")
                val time = it.getInteger("playTime", 0)
                name to time
            }
            .toList()
    }

    private fun getNameFromInput(input: Any): String? {
        return when (input) {
            is String -> input.lowercase()
            is CommandSender -> if (input is Player) input.name.lowercase() else null
            else -> null
        }
    }

    fun isShoutBanned(input: Any): Boolean {
        val name = getNameFromInput(input) ?: return false
        val doc = get(name) ?: return false
        return doc.getBoolean("shoutBanned") ?: false
    }

    fun banShout(input: Any) {
        val name = getNameFromInput(input) ?: return
        set(name, "shoutBanned", true)
    }

    fun unbanShout(input: Any) {
        val name = getNameFromInput(input) ?: return
        set(name, "shoutBanned", false)
    }

    fun getShoutRight(input: Any): Int {
        val name = getNameFromInput(input) ?: return 0
        val doc = get(name) ?: return 0
        return doc.getInteger("shoutRight", 0)
    }

    fun setShoutRight(input: Any, value: Int) {
        val name = getNameFromInput(input) ?: return
        if (isShoutBanned(name)) return
        set(name, "shoutRight", value)
    }

    fun addShoutRight(input: Any, amount: Int) {
        val name = getNameFromInput(input) ?: return
        if (isShoutBanned(name)) return
        inc(name, "shoutRight", amount)
    }

    fun removeShoutRight(input: Any, amount: Int) {
        val name = getNameFromInput(input) ?: return
        if (isShoutBanned(name)) return
        inc(name, "shoutRight", -amount)
    }

    fun hasShoutRight(input: Any, amount: Int): Boolean {
        val name = getNameFromInput(input) ?: return false
        if (isShoutBanned(name)) return false
        return getShoutRight(name) >= amount
    }

    fun setJoinMessage(name: String, message: String) {
        collection.updateOne(
            Filters.eq("name", name.lowercase()),
            Updates.set("joinMessage", message),
            UpdateOptions().upsert(true)
        )
    }

    fun getJoinMessage(name: String): String? {
        val doc = collection.find(Filters.eq("name", name.lowercase())).firstOrNull() ?: return null
        return doc.getString("joinMessage")
    }

    fun hasJoinMessage(name: String): Boolean {
        val doc = collection.find(
            Filters.and(
                Filters.eq("name", name.lowercase()),
                Filters.exists("joinMessage", true)
            )
        ).firstOrNull()
        return doc != null
    }

    fun removeJoinMessage(name: String) {
        collection.updateOne(
            Filters.eq("name", name.lowercase()),
            Updates.unset("joinMessage")
        )
    }

    fun updateJoinMessage(name: String, message: String) {
        collection.updateOne(
            Filters.eq("name", name.lowercase()),
            Updates.set("joinMessage", message),
            UpdateOptions().upsert(true)
        )
    }


    fun getAllJoinMessages(): List<Pair<String, String>> {
        return collection.find(Filters.exists("joinMessage", true))
            .map {
                val name = it.getString("name") ?: "Unknown"
                val msg = it.getString("joinMessage") ?: ""
                name to msg
            }
            .toList()
    }

    fun getRank(name: String): String? {
        val doc = get(name.lowercase()) ?: return null
        return doc.getString("rank")
    }

    fun getRankExpire(name: String): Long? {
        val doc = get(name.lowercase()) ?: return null
        return doc.getLong("rankExpire")
    }

    fun setRank(name: String, rank: String, expire: Long) {
        val filter = Filters.eq("name", name.lowercase())
        val update = Updates.combine(
            Updates.set("rank", rank),
            Updates.set("rankExpire", expire)
        )
        collection.updateOne(filter, update, UpdateOptions().upsert(true))
    }

    fun clearRank(name: String) {
        val filter = Filters.eq("name", name.lowercase())
        val update = Updates.combine(
            Updates.unset("rank"),
            Updates.unset("rankExpire")
        )
        collection.updateOne(filter, update)
    }

    fun isSettingEnabled(name: String, setting: String): Boolean {
        val doc = get(name.lowercase()) ?: return true
        val settingsDoc = doc.get("settings", Document::class.java) ?: return true
        return settingsDoc.getBoolean(setting, true)
    }

    fun setSetting(name: String, setting: String, enabled: Boolean) {
        val doc = get(name.lowercase()) ?: return
        val settingsDoc = doc.get("settings", Document::class.java)?.toMutableMap() ?: mutableMapOf()

        settingsDoc[setting] = enabled
        set(name.lowercase(), "settings", Document(settingsDoc))
        if (setting == "autoinv") {
            Utils.updateAutoInvCache(name, enabled)
        }
    }

    fun toggleSetting(name: String, setting: String): Boolean {
        val current = isSettingEnabled(name, setting)
        setSetting(name, setting, !current)
        return !current
    }

    fun getAllSettings(name: String): Map<String, Boolean> {
        val doc = get(name.lowercase()) ?: return emptyMap()
        val settingsDoc = doc.get("settings", Document::class.java) ?: return emptyMap()
        return settingsDoc.entries
            .filter { it.key != "_id" && it.key != "name" }
            .associate { it.key to (it.value as? Boolean ?: false) }
    }

    fun getKills(name: String): Int {
        createIfNotExist(name)
        val doc = collection.find(Filters.eq("name", name.lowercase())).first()
        return doc?.getInteger("kills") ?: 0
    }

    fun setKills(name: String, value: Int) {
        createIfNotExist(name)
        val fixedValue = if (value < 0) 0 else value
        collection.updateOne(
            Filters.eq("name", name.lowercase()),
            Updates.set("kills", fixedValue),
            UpdateOptions().upsert(true)
        )
    }

    fun addKills(name: String, amount: Int) {
        createIfNotExist(name)
        collection.updateOne(
            Filters.eq("name", name.lowercase()),
            Updates.inc("kills", amount),
            UpdateOptions().upsert(true)
        )
    }

    fun removeKills(name: String, amount: Int) {
        addKills(name, -amount)
    }

    fun hasKills(name: String, amount: Int): Boolean {
        return getKills(name) >= amount
    }

    fun getDeaths(name: String): Int {
        createIfNotExist(name)
        val doc = collection.find(Filters.eq("name", name.lowercase())).first()
        return doc?.getInteger("deaths") ?: 0
    }

    fun setDeaths(name: String, value: Int) {
        createIfNotExist(name)
        val fixedValue = if (value < 0) 0 else value
        collection.updateOne(
            Filters.eq("name", name.lowercase()),
            Updates.set("deaths", fixedValue),
            UpdateOptions().upsert(true)
        )
    }

    fun addDeaths(name: String, amount: Int) {
        createIfNotExist(name)
        collection.updateOne(
            Filters.eq("name", name.lowercase()),
            Updates.inc("deaths", amount),
            UpdateOptions().upsert(true)
        )
    }

    fun removeDeaths(name: String, amount: Int) {
        addDeaths(name, -amount)
    }

    fun hasDeaths(name: String, amount: Int): Boolean {
        return getDeaths(name) >= amount
    }

    fun getKDR(name: String): Double {
        val kills = getKills(name)
        val deaths = getDeaths(name)
        return if (deaths == 0) kills.toDouble() else kills.toDouble() / deaths
    }

    fun topKills(page: Int = 0, limit: Int = DEFAULT_PAGE_LIMIT): List<Pair<String, Int>> {
        return collection.find()
            .sort(Document("kills", -1))
            .skip(page * limit)
            .limit(limit)
            .map { Pair(it.getString("name"), it.getInteger("kills", 0)) }
            .toList()
    }

    fun topDeaths(page: Int = 0, limit: Int = DEFAULT_PAGE_LIMIT): List<Pair<String, Int>> {
        return collection.find()
            .sort(Document("deaths", -1))
            .skip(page * limit)
            .limit(limit)
            .map { Pair(it.getString("name"), it.getInteger("deaths", 0)) }
            .toList()
    }

    fun getFirstJoinUnix(name: String): Long? {
        val doc = collection.find(Filters.eq("name", name.lowercase())).firstOrNull()
        return doc?.getLong("firstJoinUnix")
    }

    fun getFirstJoinFormatted(name: String): String? {
        val doc = collection.find(Filters.eq("name", name.lowercase())).firstOrNull()
        return doc?.getString("firstJoinFormatted")
    }

    fun getLastAnniversaryRewardUnix(name: String): Long? {
        val doc = collection.find(Filters.eq("name", name.lowercase())).firstOrNull()
        return doc?.getLong("lastAnniversaryReward")
    }

    fun setLastAnniversaryRewardUnix(name: String, unixTime: Long) {
        collection.updateOne(
            Filters.eq("name", name.lowercase()),
            Updates.set("lastAnniversaryReward", unixTime)
        )
    }

    fun getBadges(name: String): List<String> {
        val doc = collection.find(Filters.eq("name", name.lowercase())).firstOrNull()
        return doc?.getList("badges", String::class.java) ?: emptyList()
    }

    fun addBadge(name: String, badge: String) {
        collection.updateOne(
            Filters.eq("name", name.lowercase()),
            Updates.addToSet("badges", badge)
        )
    }

    fun removeBadge(name: String, badge: String) {
        collection.updateOne(
            Filters.eq("name", name.lowercase()),
            Updates.pull("badges", badge)
        )
    }

    fun updateLoginStreak(name: String): Boolean {
        val now = LocalDateTime.now()
        val today = now.toLocalDate()
        val yesterday = today.minusDays(1)
        val formattedNow = now.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))

        val doc = collection.find(Filters.eq("name", name.lowercase())).firstOrNull() ?: return true

        val lastJoinStr = doc.getString("lastJoin").orEmpty()
        val lastJoinDate = runCatching {
            LocalDate.parse(lastJoinStr.take(10), DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        }.getOrNull()

        val currentStreak = doc.getInteger("loginStreak", 1)

        val (newStreak, streakContinues) = when {
            lastJoinDate == yesterday -> currentStreak + 1 to true
            lastJoinDate == today -> currentStreak to true
            else -> 1 to false
        }

        collection.updateOne(
            Filters.eq("name", name.lowercase()),
            Updates.combine(
                Updates.set("lastJoin", formattedNow),
                Updates.set("loginStreak", newStreak)
            )
        )

        return streakContinues
    }


}
