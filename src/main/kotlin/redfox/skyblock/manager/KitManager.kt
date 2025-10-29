package redfox.skyblock.form.kit.manager

import cn.nukkit.item.Item
import cn.nukkit.item.enchantment.Enchantment
import com.mongodb.client.MongoCollection
import org.bson.Document
import redfox.skyblock.data.Database
import java.util.concurrent.TimeUnit

object KitManager {
    lateinit var collection: MongoCollection<Document>

    val kitContents: Map<String, List<String>> = mapOf(
        "Oyuncu" to listOf("stone_sword:1:unbreaking=1", "bread:10"),
        "Nova" to listOf("iron_sword:1:sharpness=1", "bread:16"),
        "Meta" to listOf("diamond_sword:1:sharpness=3,unbreaking=2", "golden_apple:2"),
        "Prime" to listOf("diamond_sword:1:sharpness=4,unbreaking=3", "golden_apple:4"),
        "VIP" to listOf("diamond_helmet:1:protection=2", "diamond_chestplate:1:protection=2"),
        "VIP+" to listOf("netherite_sword:1:sharpness=5,unbreaking=3", "golden_apple:8"),
        "MVIP" to listOf("netherite_helmet:1:protection=4", "netherite_chestplate:1:protection=4")
    )

    const val cooldownSeconds = 7 * 24 * 60 * 60L // 7 gün

    fun canTakeKit(player: String, kit: String): Boolean {
        val now = System.currentTimeMillis() / 1000
        val lastTime = Database.getLastKitTakenTime(player, kit) ?: return true
        return now >= lastTime + cooldownSeconds
    }

    fun setKitTaken(player: String, kit: String) {
        val now = System.currentTimeMillis() / 1000
        Database.addOrUpdateKit(player, kit, now)
    }


    fun getKitItems(kit: String): List<Item> {
        val rawItems = kitContents[kit] ?: return emptyList()
        val result = mutableListOf<Item>()

        for (line in rawItems) {
            val parts = line.split(":").map { it.trim() }
            val item = Item.get(parts[0])
            if (parts.size >= 2) {
                item.count = parts[1].toIntOrNull() ?: 1
            }

            if (parts.size == 3) {
                val enchants = parts[2].split(",").map { it.trim() }
                for (e in enchants) {
                    val splitEnch = e.split("=")
                    if (splitEnch.size != 2) continue
                    val name = splitEnch[0].lowercase()
                    val level = splitEnch[1].toIntOrNull() ?: continue
                    val enchant = Enchantment.getEnchantment(name)?.setLevel(level)
                    if (enchant != null) {
                        item.addEnchantment(enchant)
                    }
                }
            }

            result.add(item)
        }

        return result
    }

    fun getNextAvailableTime(player: String, kit: String): Long? {
        val lastTaken = Database.getLastKitTakenTime(player, kit) ?: return null
        return lastTaken + cooldownSeconds
    }


    fun formatTime(secondsLeft: Long): String {
        if (secondsLeft <= 0) return "Süre doldu!"

        val days = TimeUnit.SECONDS.toDays(secondsLeft)
        val hours = TimeUnit.SECONDS.toHours(secondsLeft) % 24
        val minutes = TimeUnit.SECONDS.toMinutes(secondsLeft) % 60
        val seconds = secondsLeft % 60

        return "$days gün, $hours saat, $minutes dakika, $seconds saniye"
    }
}
