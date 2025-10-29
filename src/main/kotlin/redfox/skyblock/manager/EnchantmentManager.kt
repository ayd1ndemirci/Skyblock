package redfox.skyblock.manager

import cn.nukkit.item.Item
import cn.nukkit.item.enchantment.Enchantment

object EnchantmentManager {

    fun getApplicableEnchantments(item: Item): List<Enchantment> {
        return when {
            item.isPickaxe -> listOf(
                Enchantment.getEnchantment(Enchantment.ID_EFFICIENCY),
                Enchantment.getEnchantment(Enchantment.ID_FORTUNE_DIGGING),
                Enchantment.getEnchantment(Enchantment.ID_DURABILITY)
            )

            item.isSword -> listOf(
                Enchantment.getEnchantment(Enchantment.ID_DAMAGE_SMITE),
                Enchantment.getEnchantment(Enchantment.ID_LOOTING),
                Enchantment.getEnchantment(Enchantment.ID_FIRE_ASPECT)
            )

            item.isArmor -> listOf(
                Enchantment.getEnchantment(Enchantment.ID_PROTECTION_ALL),
                Enchantment.getEnchantment(Enchantment.ID_THORNS),
                Enchantment.getEnchantment(Enchantment.ID_DURABILITY)
            )

            item.isAxe -> listOf(
                Enchantment.getEnchantment(Enchantment.ID_EFFICIENCY),
                Enchantment.getEnchantment(Enchantment.ID_DURABILITY)
            )

            item.isBow -> listOf(
                Enchantment.getEnchantment(Enchantment.ID_BOW_POWER),
                Enchantment.getEnchantment(Enchantment.ID_BOW_FLAME),
                Enchantment.getEnchantment(Enchantment.ID_BOW_INFINITY)
            )

            else -> emptyList()
        }.filterNotNull()
    }

    private val Item.isPickaxe
        get() = id in listOf(
            Item.WOODEN_PICKAXE,
            Item.STONE_PICKAXE,
            Item.IRON_PICKAXE,
            Item.GOLDEN_PICKAXE,
            Item.DIAMOND_PICKAXE
        )
    private val Item.isSword
        get() = id in listOf(
            Item.WOODEN_SWORD,
            Item.STONE_SWORD,
            Item.IRON_SWORD,
            Item.GOLDEN_SWORD,
            Item.DIAMOND_SWORD
        )
    private val Item.isAxe
        get() = id in listOf(
            Item.WOODEN_AXE,
            Item.STONE_AXE,
            Item.IRON_AXE,
            Item.GOLDEN_AXE,
            Item.DIAMOND_AXE
        )
    private val Item.isArmor
        get() = id in listOf(
            Item.LEATHER_CHESTPLATE,
            Item.CHAINMAIL_CHESTPLATE,
            Item.IRON_CHESTPLATE,
            Item.DIAMOND_CHESTPLATE,
            Item.GOLDEN_CHESTPLATE
        )
    private val Item.isBow get() = id == Item.BOW
}
