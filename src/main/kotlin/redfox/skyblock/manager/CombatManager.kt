package redfox.skyblock.manager

import cn.nukkit.Player
import cn.nukkit.scheduler.NukkitRunnable
import redfox.skyblock.Core

object CombatManager {

    private val combatPlayers = mutableMapOf<String, Long>()
    private const val COMBAT_DURATION_MS = 8_000L

    init {
        object : NukkitRunnable() {
            override fun run() {
                cleanExpiredCombatPlayers()
            }
        }.runTaskTimer(Core.instance, 20, 20)
    }

    fun startCombat(player: Player) {
        val name = player.name.lowercase()
        val now = System.currentTimeMillis()
        val newEndTime = now + COMBAT_DURATION_MS

        val alreadyInCombat = isInCombat(player)

        combatPlayers[name] = newEndTime

        if (!alreadyInCombat) {
            player.sendMessage("§cSavaş başladı! 8 saniye boyunca komut kullanamaz ve çıkamazsın.")
        }
    }

    fun isInCombat(player: Player): Boolean {
        val name = player.name.lowercase()
        val endTime = combatPlayers[name] ?: return false
        if (System.currentTimeMillis() >= endTime) {
            combatPlayers.remove(name)
            return false
        }
        return true
    }

    fun removePlayer(playerName: String) {
        combatPlayers.remove(playerName.lowercase())
    }

    private fun cleanExpiredCombatPlayers() {
        val now = System.currentTimeMillis()
        val expired = combatPlayers.filterValues { it < now }.keys

        for (name in expired) {
            combatPlayers.remove(name)

            val player = Core.instance.server.getPlayer(name)
            if (player != null && player.isOnline) {
                player.sendMessage("§aSavaştan çıktın! Artık güvendesin.")
            }
        }
    }
}
