package redfox.skyblock.utils

import cn.nukkit.Player
import cn.nukkit.command.CommandSender
import cn.nukkit.network.protocol.PlaySoundPacket
import redfox.skyblock.data.Database
import redfox.skyblock.data.VIP
import java.sql.SQLException
import kotlin.math.ceil

object Utils {
    const val DEFAULT_PAGE_LIMIT = 10
    val lastMessagePlayer = mutableMapOf<String, String>()
    private val cooldowns = mutableMapOf<String, Long>()
    val playerToFactionCache = mutableMapOf<String, String?>()
    val factionOnlineCache = mutableMapOf<String, Int>()
    val factionAlliesCache = mutableMapOf<String, MutableList<String>>()
    val joinTimes: MutableMap<String, Long> = mutableMapOf()
    val repairList = mutableMapOf<String, Long>()
    const val REPAIR_ITEM_PRICE: Int = 5
    private val repairingPlayers = mutableSetOf<String>()
    val statusPlayers = hashMapOf<String, String>()
    private val autoItemCache = mutableMapOf<String, Boolean>()
    const val DEFAULT_MONEY = 5000


    object BlockedWorlds {
        val forGiftRequests = setOf(Location.ARENA)
    }

    var chatLocked: Boolean = false


    fun isAdmin(p: String): Boolean {
        return p == "MrCyber3308" || p == "ayd1ndemirci"
    }

    fun isAdmin(p: CommandSender): Boolean {
        return p is Player && isAdmin(p.name)
    }

    fun isVIP(name: String): Boolean {
        val vips = VIP.getPlayerVIPs(name.lowercase())
        val now = System.currentTimeMillis() / 1000
        return vips.any { (it["time"] as? Int ?: 0) > now }
    }

    fun isVIP(p: CommandSender): Boolean {
        return p is Player && isVIP(p.name)
    }

    fun <T> page(list: List<T>, page: Int, limit: Int = DEFAULT_PAGE_LIMIT): List<T> {
        val start = page * limit
        val end = minOf(start + limit, list.size)
        return if (start < list.size) list.subList(start, end) else emptyList()
    }

    fun pageCount(count: Int, limit: Int = DEFAULT_PAGE_LIMIT): Int {
        return ceil(count / limit.toDouble()).toInt()
    }

    fun checkCooldown(key: String, cooldownMillis: Long): Boolean {
        val currentTime = System.currentTimeMillis()

        if (currentTime - (cooldowns[key] ?: 0L) >= cooldownMillis) {
            cooldowns[key] = currentTime
            return true
        }

        return false
    }

    fun generatePassword(): String {
        val randomChar = ('a'..'z').random()
        return "RF${(10000..99999).random()}$randomChar"
    }


    @Throws(SQLException::class)
    fun getPlayTimeConverter(playerName: String): String {
        val time = Database.getPlayTime(playerName).toLong()

        var remainingTime = time

        val month = remainingTime / (3600 * 24 * 30)
        remainingTime %= (3600 * 24 * 30)

        val day = remainingTime / (3600 * 24)
        remainingTime %= (3600 * 24)

        val hour = remainingTime / 3600
        remainingTime %= 3600

        val minute = remainingTime / 60
        val second = remainingTime % 60

        return when {
            month > 0 -> "$month ay $day gün $hour saat $minute dakika $second saniye"
            day > 0 -> "$day gün $hour saat $minute dakika $second saniye"
            hour > 0 -> "$hour saat $minute dakika $second saniye"
            minute > 0 -> "$minute dakika $second saniye"
            else -> "$second saniye"
        }
    }

    fun isPlayerOnTheRepairList(playerName: String): Boolean {
        return repairList.containsKey(playerName)
    }

    fun addRepairList(playerName: String) {
        val currentTime = System.currentTimeMillis() / 1000
        repairList[playerName] = currentTime + 300
    }

    fun removePlayerRepairList(playerName: String) {
        repairList.remove(playerName)
    }

    fun getRepairRemainingTime(playerName: String): Long {
        return repairList[playerName] ?: 0L
    }

    fun startRepairing(playerName: String) {
        repairingPlayers.add(playerName)
    }

    fun stopRepairing(playerName: String) {
        repairingPlayers.remove(playerName)
    }

    fun isRepairing(playerName: String): Boolean = repairingPlayers.contains(playerName)


    fun isAutoInvEnabled(name: String): Boolean {
        return autoItemCache.getOrPut(name) {
            Database.isSettingEnabled(name, "autoinv")
        }
    }

    fun updateAutoInvCache(name: String, value: Boolean) {
        autoItemCache[name] = value
    }

    fun invalidateAutoInvCache(name: String) {
        autoItemCache.remove(name)
    }

    @JvmStatic
    fun isInvalidGroupName(groupName: String): Boolean = !"^[a-zA-Z0-9_]+$".toRegex().matches(groupName)

    fun sound(
        player: Player,
        soundName: String,
        allowOthers: Boolean = false,
        volume: Float = 1f,
        pitch: Float = 1f,
        radius: Double = 4.0,
    ) {
        val pos = player.position
        val packet = PlaySoundPacket().apply {
            name = soundName
            this.volume = volume
            this.pitch = pitch
            x = pos.floorX
            y = pos.floorY
            z = pos.floorZ
        }

        player.dataPacket(packet)

        if (allowOthers) {
            player.level.players.values
                .filter { it != player && it.position.distance(pos) <= radius }
                .forEach { it.dataPacket(packet) }
        }
    }
}