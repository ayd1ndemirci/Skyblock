package redfox.skyblock.event

import cn.nukkit.Player
import cn.nukkit.event.EventHandler
import cn.nukkit.event.Listener
import cn.nukkit.event.block.BlockBreakEvent
import cn.nukkit.event.entity.EntityDamageEvent
import cn.nukkit.level.Sound
import redfox.skyblock.data.Database
import java.util.*

class ItemDurability : Listener {

    @EventHandler
    fun onItemDurability(event: BlockBreakEvent) {
        if (event.isCancelled) return

        val player: Player = event.player

        if (!Database.isSettingEnabled(player.name, "durability")) return

        val item = player.inventory.itemInHand ?: return

        if (!item.isTool && !item.isArmor && !item.isSword && !item.isPickaxe && !item.isAxe && !item.isShovel) return

        val maxDamage = item.maxDurability
        val damage = item.damage
        val remaining = maxDamage - damage
        val percentage = ((remaining - 1).toDouble() / maxDamage.toDouble()) * 100
        val roundedString = String.format(Locale.US, "%.2f", percentage)
        val rounded = roundedString.toDouble()

        val color = when {
            rounded >= 75 -> "§a"
            rounded >= 20 -> "§e"
            else -> "§c"
        }

        if (rounded <= 5) {
            player.level.addSound(player.position, Sound.RANDOM_CLICK, 1f, 1f, player)
        }

        player.sendActionBar("§7Dayanıklılık: $color$rounded%")
    }

    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        val player = event.entity as? Player ?: return

        val messages = mutableListOf<String>()

        val armorItems = player.inventory.armorContents

        for (item in armorItems) {
            if (item.maxDurability > 0 && item.damage >= item.maxDurability - 10) {
                val percent = 100 - ((item.damage.toDouble() / item.maxDurability) * 100).toInt()
                messages.add("§c${item.name} (%$percent)")
                player.level.addSound(player.position, Sound.RANDOM_CLICK, 1f, 1f, player)
            }
        }

        if (messages.isNotEmpty()) {
            player.sendPopup("§7Kırılmak üzere: " + messages.joinToString(" §8| "))
        }
    }
}
