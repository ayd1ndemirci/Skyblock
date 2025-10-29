package redfox.skyblock.manager

import cn.nukkit.item.Item
import cn.nukkit.item.ItemID
import cn.nukkit.item.enchantment.Enchantment
import redfox.skyblock.model.Reward
import redfox.skyblock.model.RewardEntry

object RewardManager {

    val rewardList = listOf(
        RewardEntry("16x Steak", "textures/items/beef_cooked.png", Reward.ItemReward(Item.get(ItemID.COOKED_BEEF), 16)),
        RewardEntry("32x Ekmek", "textures/items/bread.png", Reward.ItemReward(Item.get(ItemID.BREAD), 32)),
        RewardEntry("10x Elmas", "textures/items/diamond.png", Reward.ItemReward(Item.get(ItemID.DIAMOND), 10)),
        RewardEntry(
            "5x Altın Külçesi",
            "textures/items/gold_ingot.png",
            Reward.ItemReward(Item.get(ItemID.GOLD_INGOT), 5)
        ),
        RewardEntry("Büyülü Elmas Kılıç", "textures/items/diamond_sword.png", createEnchantedSword()),
        RewardEntry("Büyülü Demir Kılıç", "textures/items/iron_sword.png", createEnchantedIronSword()),
        RewardEntry(
            "Koruyucu Büyülü Elmas Göğüs Zırhı",
            "textures/items/diamond_chestplate.png",
            createEnchantedChestplate()
        ),
        RewardEntry("Koruyucu Büyülü Demir Miğfer", "textures/items/iron_helmet.png", createEnchantedHelmet()),
        RewardEntry("Büyülü Yay", "textures/items/bow.png", createEnchantedBow()),
        RewardEntry("100 Kredi", "textures/items/emerald.png", Reward.MoneyReward(100)),
        RewardEntry("250 Kredi", "textures/items/emerald.png", Reward.MoneyReward(250)),
        RewardEntry("500 Kredi", "textures/items/emerald.png", Reward.MoneyReward(500)),
        RewardEntry(
            "Kutlama Mesajı 1",
            "textures/ui/icon_book_writable.png",
            Reward.MessageReward("§bBugünün mesajı: İyi oyunlar!")
        ),
        RewardEntry(
            "Kutlama Mesajı 2",
            "textures/ui/icon_book_writable.png",
            Reward.MessageReward("§aBaşarılar dileriz!")
        ),
        RewardEntry("16x Ok", "textures/items/arrow.png", Reward.ItemReward(Item.get(ItemID.ARROW), 16)),
        RewardEntry(
            "10x Altın Elma",
            "textures/items/golden_apple.png",
            Reward.ItemReward(Item.get(ItemID.GOLDEN_APPLE), 10)
        ),
        RewardEntry("Büyülü Elmas Kazma", "textures/items/diamond_pickaxe.png", createEnchantedPickaxe()),
        RewardEntry("Büyülü Demir Balta", "textures/items/iron_axe.png", createEnchantedAxe()),
        RewardEntry("Büyülü Elmas Kürek", "textures/items/diamond_shovel.png", createEnchantedShovel()),
        RewardEntry("Büyülü Elmas Kask", "textures/items/diamond_helmet.png", createEnchantedHelmetDiamond()),
        RewardEntry("Büyülü Elmas Çizme", "textures/items/diamond_boots.png", createEnchantedBoots()),
        RewardEntry("Büyülü Elytra", "textures/items/elytra.png", createEnchantedElytra()),
        RewardEntry(
            "20x Koyun Yünü",
            "textures/items/wool_colored_white.png",
            Reward.ItemReward(Item.get(ItemID.WOOL), 20)
        ),
        RewardEntry(
            "15x Elmas Kılıç",
            "textures/items/diamond_sword.png",
            Reward.ItemReward(Item.get(ItemID.DIAMOND_SWORD), 15)
        ),
        RewardEntry("Büyülü Altın Kılıç", "textures/items/gold_sword.png", createEnchantedGoldSword()),
        RewardEntry("Büyülü Demir Göğüs Zırhı", "textures/items/iron_chestplate.png", createEnchantedIronChestplate()),
        RewardEntry("5x Elytra", "textures/items/elytra.png", Reward.ItemReward(Item.get(ItemID.ELYTRA), 5)),
        RewardEntry("20x Pusula", "textures/items/compass.png", Reward.ItemReward(Item.get(ItemID.COMPASS), 20)),
        RewardEntry("Büyülü Taş Kılıç", "textures/items/stone_sword.png", createEnchantedStoneSword()),
        RewardEntry("Büyülü Demir Çizme", "textures/items/iron_boots.png", createEnchantedIronBoots()),
    )

    fun getAllRewards(): List<RewardEntry> = rewardList

    private fun createEnchantedSword(): Reward.ItemReward {
        val sword = Item.get(ItemID.DIAMOND_SWORD)
        Enchantment.getEnchantment(Enchantment.ID_DAMAGE_ALL)?.let {
            sword.addEnchantment(it.setLevel(3))
        }
        Enchantment.getEnchantment(Enchantment.ID_DURABILITY)?.let {
            sword.addEnchantment(it.setLevel(2))
        }
        return Reward.ItemReward(sword)
    }

    private fun createEnchantedIronSword(): Reward.ItemReward {
        val sword = Item.get(ItemID.IRON_SWORD)
        Enchantment.getEnchantment(Enchantment.ID_DAMAGE_SMITE)?.let {
            sword.addEnchantment(it.setLevel(2))
        }
        Enchantment.getEnchantment(Enchantment.ID_DURABILITY)?.let {
            sword.addEnchantment(it.setLevel(2))
        }
        return Reward.ItemReward(sword)
    }

    private fun createEnchantedChestplate(): Reward.ItemReward {
        val chestplate = Item.get(ItemID.DIAMOND_CHESTPLATE)
        Enchantment.getEnchantment(Enchantment.ID_PROTECTION_ALL)?.let {
            chestplate.addEnchantment(it.setLevel(3))
        }
        Enchantment.getEnchantment(Enchantment.ID_THORNS)?.let {
            chestplate.addEnchantment(it.setLevel(1))
        }
        return Reward.ItemReward(chestplate)
    }

    private fun createEnchantedHelmet(): Reward.ItemReward {
        val helmet = Item.get(ItemID.IRON_HELMET)
        Enchantment.getEnchantment(Enchantment.ID_PROTECTION_ALL)?.let {
            helmet.addEnchantment(it.setLevel(2))
        }
        Enchantment.getEnchantment(Enchantment.ID_DURABILITY)?.let {
            helmet.addEnchantment(it.setLevel(2))
        }
        return Reward.ItemReward(helmet)
    }

    private fun createEnchantedBow(): Reward.ItemReward {
        val bow = Item.get(ItemID.BOW)
        Enchantment.getEnchantment(Enchantment.ID_BOW_POWER)?.let {
            bow.addEnchantment(it.setLevel(3))
        }
        Enchantment.getEnchantment(Enchantment.ID_BOW_FLAME)?.let {
            bow.addEnchantment(it.setLevel(1))
        }
        Enchantment.getEnchantment(Enchantment.ID_DURABILITY)?.let {
            bow.addEnchantment(it.setLevel(2))
        }
        return Reward.ItemReward(bow)
    }

    private fun createEnchantedPickaxe(): Reward.ItemReward {
        val pickaxe = Item.get(ItemID.DIAMOND_PICKAXE)
        Enchantment.getEnchantment(Enchantment.ID_EFFICIENCY)?.let {
            pickaxe.addEnchantment(it.setLevel(3))
        }
        Enchantment.getEnchantment(Enchantment.ID_DURABILITY)?.let {
            pickaxe.addEnchantment(it.setLevel(2))
        }
        return Reward.ItemReward(pickaxe)
    }

    private fun createEnchantedAxe(): Reward.ItemReward {
        val axe = Item.get(ItemID.IRON_AXE)
        Enchantment.getEnchantment(Enchantment.ID_DAMAGE_ALL)?.let {
            axe.addEnchantment(it.setLevel(3))
        }
        Enchantment.getEnchantment(Enchantment.ID_DURABILITY)?.let {
            axe.addEnchantment(it.setLevel(2))
        }
        return Reward.ItemReward(axe)
    }

    private fun createEnchantedShovel(): Reward.ItemReward {
        val shovel = Item.get(ItemID.DIAMOND_SHOVEL)
        Enchantment.getEnchantment(Enchantment.ID_EFFICIENCY)?.let {
            shovel.addEnchantment(it.setLevel(3))
        }
        Enchantment.getEnchantment(Enchantment.ID_DURABILITY)?.let {
            shovel.addEnchantment(it.setLevel(2))
        }
        return Reward.ItemReward(shovel)
    }

    private fun createEnchantedHelmetDiamond(): Reward.ItemReward {
        val helmet = Item.get(ItemID.DIAMOND_HELMET)
        Enchantment.getEnchantment(Enchantment.ID_PROTECTION_ALL)?.let {
            helmet.addEnchantment(it.setLevel(3))
        }
        Enchantment.getEnchantment(Enchantment.ID_THORNS)?.let {
            helmet.addEnchantment(it.setLevel(1))
        }
        Enchantment.getEnchantment(Enchantment.ID_DURABILITY)?.let {
            helmet.addEnchantment(it.setLevel(2))
        }
        return Reward.ItemReward(helmet)
    }

    private fun createEnchantedBoots(): Reward.ItemReward {
        val boots = Item.get(ItemID.DIAMOND_BOOTS)
        Enchantment.getEnchantment(Enchantment.ID_PROTECTION_FALL)?.let {
            boots.addEnchantment(it.setLevel(3))
        }
        Enchantment.getEnchantment(Enchantment.ID_PROTECTION_ALL)?.let {
            boots.addEnchantment(it.setLevel(2))
        }
        return Reward.ItemReward(boots)
    }

    private fun createEnchantedElytra(): Reward.ItemReward {
        val elytra = Item.get(ItemID.ELYTRA)
        Enchantment.getEnchantment(Enchantment.ID_DURABILITY)?.let {
            elytra.addEnchantment(it.setLevel(2))
        }
        return Reward.ItemReward(elytra)
    }

    private fun createEnchantedGoldSword(): Reward.ItemReward {
        val sword = Item.get(ItemID.GOLDEN_SWORD)
        Enchantment.getEnchantment(Enchantment.ID_DAMAGE_ALL)?.let {
            sword.addEnchantment(it.setLevel(2))
        }
        Enchantment.getEnchantment(Enchantment.ID_FIRE_ASPECT)?.let {
            sword.addEnchantment(it.setLevel(1))
        }
        return Reward.ItemReward(sword)
    }

    private fun createEnchantedIronChestplate(): Reward.ItemReward {
        val chestplate = Item.get(ItemID.IRON_CHESTPLATE)
        Enchantment.getEnchantment(Enchantment.ID_PROTECTION_ALL)?.let {
            chestplate.addEnchantment(it.setLevel(2))
        }
        Enchantment.getEnchantment(Enchantment.ID_THORNS)?.let {
            chestplate.addEnchantment(it.setLevel(1))
        }
        return Reward.ItemReward(chestplate)
    }

    private fun createEnchantedStoneSword(): Reward.ItemReward {
        val sword = Item.get(ItemID.STONE_SWORD)
        Enchantment.getEnchantment(Enchantment.ID_DAMAGE_ALL)?.let {
            sword.addEnchantment(it.setLevel(1))
        }
        Enchantment.getEnchantment(Enchantment.ID_DURABILITY)?.let {
            sword.addEnchantment(it.setLevel(1))
        }
        return Reward.ItemReward(sword)
    }

    private fun createEnchantedIronBoots(): Reward.ItemReward {
        val boots = Item.get(ItemID.IRON_BOOTS)
        Enchantment.getEnchantment(Enchantment.ID_PROTECTION_FALL)?.let {
            boots.addEnchantment(it.setLevel(2))
        }
        Enchantment.getEnchantment(Enchantment.ID_PROTECTION_ALL)?.let {
            boots.addEnchantment(it.setLevel(1))
        }
        return Reward.ItemReward(boots)
    }
}
