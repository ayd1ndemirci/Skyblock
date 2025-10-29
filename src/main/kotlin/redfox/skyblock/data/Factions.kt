package redfox.skyblock.data

import cn.nukkit.Player
import com.mongodb.client.model.CountOptions
import com.mongodb.client.model.Filters
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import org.bson.Document
import redfox.skyblock.data.MongoDB.db
import redfox.skyblock.utils.Utils
import redfox.skyblock.utils.Utils.DEFAULT_PAGE_LIMIT

object Factions {

    init {
        Configurator.setLevel("org.mongodb.driver", Level.OFF)
    }


    private val factions = db.getCollection("skyblock")
    private val members = db.getCollection("members")
    private val alliances = db.getCollection("alliances")
    private val requests = db.getCollection("requests")
    private val chunks = db.getCollection("chunks")
    val warRequests = mutableListOf<Pair<String, String>>()
    var ongoingWar: Pair<String, String>? = null

    enum class FactionMemberRole {
        MEMBER, OFFICER, OWNER
    }

    enum class FactionRequestType {
        MEMBER, ALLIANCE
    }

    enum class FactionRequestDirection {
        INCOMING, OUTGOING
    }

    fun get(name: String): Document? {
        return factions.find(Filters.eq("name", name)).first()
    }

    fun exists(name: String): Boolean {
        return factions.countDocuments(Filters.eq("name", name), CountOptions().limit(1)) > 0
    }

    fun create(name: String, owner: String): Document {
        val factionDoc = Document("name", name)
            .append("owner", owner)
            .append("description", "")
            .append("power", 0)
            .append("money", 0)
            .append("bannedPlayers", listOf<String>())
            .append("createdAt", System.currentTimeMillis())
        factions.insertOne(factionDoc)
        setMember(name, owner, FactionMemberRole.OWNER)
        return factionDoc
    }

    fun getMembers(factionName: String): List<Document> {
        return members.find(Filters.eq("factionName", factionName)).toList()
    }

    fun getMembersBelowRole(
        factionName: String,
        role: FactionMemberRole
    ): List<Document> {
        return members.find(
            Filters.and(
                Filters.eq("factionName", factionName),
                Filters.lt("role", role.ordinal)
            )
        ).toList()
    }

    fun getBannedMembers(factionName: String): List<String> {
        val faction = get(factionName) ?: return emptyList()
        return faction.getList("bannedPlayers", String::class.java)
    }

    fun addBan(factionName: String, playerName: String) {
        val faction = get(factionName) ?: return
        val bannedPlayers = faction.getList("bannedPlayers", String::class.java).toMutableList()
        if (!bannedPlayers.contains(playerName)) {
            bannedPlayers.add(playerName)
            factions.updateOne(
                Filters.eq("name", factionName),
                Document("\$set", Document("bannedPlayers", bannedPlayers))
            )
            if (getMemberFactionName(playerName) != factionName) {
                removeMember(playerName)
            }
        }
    }

    fun removeBan(factionName: String, playerName: String): Boolean {
        val faction = get(factionName) ?: return false
        val bannedPlayers = faction.getList("bannedPlayers", String::class.java).toMutableList()
        if (bannedPlayers.remove(playerName)) {
            factions.updateOne(
                Filters.eq("name", factionName),
                Document("\$set", Document("bannedPlayers", bannedPlayers))
            )
            return true
        }
        return false
    }

    fun getMember(memberName: String): Document? {
        return members.find(Filters.eq("name", memberName)).first()
    }

    fun getMember(player: Player): Document? {
        return getMember(player.name)
    }

    fun getMemberFactionName(memberName: String): String? {
        if (!Utils.playerToFactionCache.containsKey(memberName)) {
            return getMember(memberName)?.getString("factionName")
        }
        return Utils.playerToFactionCache[memberName]
    }

    fun getMemberRole(player: Player): Int? {
        return getMember(player)?.getInteger("role")
    }

    fun getMemberFaction(player: Player): Document? {
        val member = getMember(player)
        return if (member != null) get(member.getString("factionName")) else null
    }

    fun hasRequest(from: String, to: String, type: FactionRequestType): Boolean {
        return requests.find(
            Filters.and(
                Filters.eq("from", from),
                Filters.eq("to", to),
                Filters.eq("type", type.ordinal)
            )
        ).firstOrNull() != null
    }

    fun getRequests(name: String, direction: FactionRequestDirection, type: FactionRequestType): List<Document> {
        return requests.find(
            Filters.and(
                Filters.eq(
                    if (direction == FactionRequestDirection.INCOMING) "to" else "from", name
                ),
                Filters.eq("type", type.ordinal)
            )
        ).toList()
    }

    fun normalizePair(f1: String, f2: String): Pair<String, String> {
        return if (f1 < f2) Pair(f1, f2) else Pair(f2, f1)
    }

    fun getAlliances(factionName: String, cache: Boolean = true): List<String> {
        if (cache && Utils.factionAlliesCache.containsKey(factionName)) {
            return Utils.factionAlliesCache[factionName] ?: emptyList()
        }
        return alliances.find(
            Filters.or(
                Filters.eq("a", factionName),
                Filters.eq("b", factionName)
            )
        ).toList().map { doc ->
            val a = doc.getString("a")
            val b = doc.getString("b")
            if (a == factionName) b else a
        }
    }

    fun areAllies(factionName: String, allyName: String): Boolean {
        val cache = Utils.factionAlliesCache[factionName]
        if (cache != null) return cache.contains(allyName)
        val cache2 = Utils.factionAlliesCache[allyName]
        if (cache2 != null) return cache2.contains(factionName)

        if (factionName == allyName) return false
        val (a, b) = normalizePair(factionName, allyName)
        return alliances.find(Filters.and(Filters.eq("a", a), Filters.eq("b", b))).first() != null
    }

    fun canHitPlayer(player: Player, target: Player): Boolean {
        if (Utils.isAdmin(player)) return true
        val playerFaction = getMemberFactionName(player.name)
        val targetFaction = getMemberFactionName(target.name)
        if (playerFaction == null || targetFaction == null) return true
        if (playerFaction == targetFaction) return true
        return !areAllies(playerFaction, targetFaction)
    }

    fun addAlliance(factionName: String, allyName: String) {
        if (factionName == allyName) return
        if (areAllies(factionName, allyName)) return

        val (a, b) = normalizePair(factionName, allyName)
        val allianceDoc = Document("a", a)
            .append("b", b)
            .append("createdAt", System.currentTimeMillis())
        alliances.insertOne(allianceDoc)
        if (Utils.factionAlliesCache.containsKey(factionName)) Utils.factionAlliesCache[factionName]!!.add(allyName)
        if (Utils.factionAlliesCache.containsKey(allyName)) Utils.factionAlliesCache[allyName]!!.add(factionName)
    }

    fun removeAlliance(factionName: String, allyName: String) {
        val (a, b) = normalizePair(factionName, allyName)
        alliances.deleteOne(Filters.and(Filters.eq("a", a), Filters.eq("b", b)))
    }

    fun addRequest(from: String, to: String, type: FactionRequestType): Boolean {
        val requestExists = requests.countDocuments(
            Filters.and(
                Filters.eq("from", from),
                Filters.eq("to", to),
                Filters.eq("type", type.ordinal)
            ), CountOptions().limit(1)
        ) > 0

        if (requestExists) return false

        requests.insertOne(
            Document("from", from)
                .append("to", to)
                .append("type", type.ordinal)
                .append("createdAt", System.currentTimeMillis())
        )

        return true
    }

    fun removeRequest(from: String, to: String, type: FactionRequestType) {
        requests.deleteOne(
            Filters.and(
                Filters.eq("from", from),
                Filters.eq("to", to),
                Filters.eq("type", type.ordinal)
            )
        )
    }

    fun removeMemberRequests(name: String) {
        requests.deleteMany(
            Filters.and(
                Filters.eq("from", name),
                Filters.eq("type", FactionRequestType.MEMBER.ordinal)
            )
        )
    }

    fun setMember(factionName: String, memberName: String, role: FactionMemberRole): Int {
        val member = getMember(memberName)
        if (member != null) {
            if (member.getString("factionName") != factionName) return 0
            members.updateOne(
                Filters.eq("_id", member.getObjectId("_id")),
                Document("\$set", Document("role", role.ordinal))
            )
            return 1
        }

        val oldFactionName = Utils.playerToFactionCache[memberName]
        val memberDoc = Document("name", memberName)
            .append("factionName", factionName)
            .append("role", role.ordinal)
            .append("joinedAt", System.currentTimeMillis())
        members.insertOne(memberDoc)
        if (oldFactionName != null && oldFactionName != factionName) {
            val online = Utils.factionOnlineCache.getOrDefault(oldFactionName, 0)
            Utils.factionOnlineCache[oldFactionName] = online - 1
            if (online == 1) Utils.factionAlliesCache.remove(oldFactionName)
            val online2 = Utils.factionOnlineCache.getOrDefault(factionName, 0)
            Utils.factionOnlineCache[factionName] = online2 + 1
            if (online2 == 0) Utils.factionAlliesCache[factionName] = getAlliances(factionName).toMutableList()
            Utils.playerToFactionCache[memberName] = factionName
        }
        return 2
    }

    fun removeMember(memberName: String): Boolean {
        val factionName = Utils.playerToFactionCache[memberName]
        if (factionName == null) return false
        val online = Utils.factionOnlineCache.getOrDefault(factionName, 0)
        Utils.factionOnlineCache[factionName] = online - 1
        if (online == 1) Utils.factionAlliesCache.remove(factionName)
        Utils.playerToFactionCache[memberName] = null
        return members.deleteOne(Filters.eq("name", memberName)).deletedCount > 0
    }

    fun update(name: String, updates: Document) {
        factions.updateOne(Filters.eq("name", name), Document("\$set", updates))
    }

    fun addPower(name: String, power: Int) {
        factions.updateOne(
            Filters.eq("name", name),
            Document("\$inc", Document("power", power))
        )
    }

    fun remove(name: String) {
        factions.deleteOne(Filters.eq("name", name))
        members.deleteMany(Filters.eq("factionName", name))
        alliances.deleteMany(
            Filters.or(
                Filters.eq("a", name),
                Filters.eq("b", name)
            )
        )
        requests.deleteMany(
            Filters.or(
                Filters.eq("from", name),
                Filters.eq("to", name)
            )
        )
        chunks.deleteMany(Filters.eq("factionName", name))
        Utils.playerToFactionCache.entries.removeIf { it.value == name }
        Utils.factionOnlineCache.remove(name)
        Utils.factionAlliesCache.remove(name)
    }

    fun top(page: Int = 0, limit: Int = DEFAULT_PAGE_LIMIT): List<Pair<String, Int>> {
        return factions.find()
            .projection(Document("name", 1).append("power", 1).append("_id", 0))
            .sort(Document("power", -1))
            .skip(page * limit)
            .limit(limit)
            .map { it.getString("name") to it.getInteger("power", 0) }
            .toList()
    }

    fun pageCount(limit: Int = DEFAULT_PAGE_LIMIT): Int {
        return Utils.pageCount(factions.countDocuments().toInt(), limit)
    }

    fun searchFactions(query: String, page: Int = 0, limit: Int = DEFAULT_PAGE_LIMIT): List<Pair<String, Int>> {
        return factions.find(
            Filters.or(
                Filters.eq("name", query),
                Filters.regex("name", "^$query", "i")
            )
        )
            .projection(Document("name", 1).append("power", 1).append("_id", 0))
            .sort(Document("power", -1))
            .skip(page * limit)
            .limit(limit)
            .map { it.getString("name") to it.getInteger("power", 0) }
            .toList()
    }

    fun getFactionSearchPageCount(query: String, limit: Int = DEFAULT_PAGE_LIMIT): Int {
        return Utils.pageCount(
            factions.countDocuments(
                Filters.or(
                    Filters.eq("name", query),
                    Filters.regex("name", "^$query", "i")
                )
            ).toInt(), limit
        )
    }
}