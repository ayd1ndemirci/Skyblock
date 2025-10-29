package redfox.skyblock.manager

import cn.nukkit.item.Item
import cn.nukkit.item.enchantment.Enchantment

object ShopItemManager {

    val items = listOf(

        ShopItem("Edit Kılıç\n", 50) { player ->
            val sword = Item.get(Item.DIAMOND_SWORD)
            sword.addEnchantment(Enchantment.get(Enchantment.ID_DAMAGE_SMITE)!!.setLevel(3))
            player.inventory.addItem(sword)
            player.sendMessage("§aBaşarılı bir şekilde §2Edit Kılıç §asatın aldın!")
        },

        ShopItem("Edit Kazma\n", 70) { player ->
            val pickaxe = Item.get(Item.DIAMOND_PICKAXE)
            pickaxe.addEnchantment(Enchantment.get(Enchantment.ID_EFFICIENCY)!!.setLevel(3))
            player.inventory.addItem(pickaxe)
            player.sendMessage("§aBaşarılı bir şekilde §2Edit Kazma §asatın aldın!")
        },

        ShopItem("Edit Balta\n", 30) { player ->
            val axe = Item.get(Item.DIAMOND_AXE)
            axe.addEnchantment(Enchantment.get(Enchantment.ID_EFFICIENCY)!!.setLevel(3))
            player.inventory.addItem(axe)
            player.sendMessage("§aBaşarılı bir şekilde §2Edit Balta §asatın aldın!")
        },

        ShopItem("Edit Kask\n", 40) { player ->
            val helmet = Item.get(Item.DIAMOND_HELMET)
            helmet.addEnchantment(Enchantment.get(Enchantment.ID_PROTECTION_ALL)!!.setLevel(3))
            player.inventory.addItem(helmet)
            player.sendMessage("§aBaşarılı bir şekilde §2Edit Kask §asatın aldın!")
        },

        ShopItem("Edit Göğüslük\n", 40) { player ->
            val chestplate = Item.get(Item.DIAMOND_CHESTPLATE)
            chestplate.addEnchantment(Enchantment.get(Enchantment.ID_PROTECTION_ALL)!!.setLevel(3))
            player.inventory.addItem(chestplate)
            player.sendMessage("§aBaşarılı bir şekilde §2Edit Göğüslük §asatın aldın!")
        },

        ShopItem("Edit Pantolon\n", 40) { player ->
            val leggings = Item.get(Item.DIAMOND_LEGGINGS)
            leggings.addEnchantment(Enchantment.get(Enchantment.ID_PROTECTION_ALL)!!.setLevel(3))
            player.inventory.addItem(leggings)
            player.sendMessage("§aBaşarılı bir şekilde §2Edit Pantolon §asatın aldın!")
        },

        ShopItem("Edit Bot\n", 40) { player ->
            val boots = Item.get(Item.DIAMOND_BOOTS)
            boots.addEnchantment(Enchantment.get(Enchantment.ID_PROTECTION_ALL)!!.setLevel(3))
            player.inventory.addItem(boots)
            player.sendMessage("§aBaşarılı bir şekilde §2Edit Bot §asatın aldın!")
        },

        ShopItem("Elytra\n", 100) { player ->
            val elytra = Item.get(Item.ELYTRA)
            elytra.addEnchantment(Enchantment.get(Enchantment.ID_DURABILITY)!!.setLevel(5))
            player.inventory.addItem(elytra)
            player.sendMessage("§aBaşarılı bir şekilde §2Elytra §asatın aldın!")
        },
        ShopItem("Balyoz\n", 100) { player ->
            val mace = Item.get(Item.MACE)
            player.inventory.addItem(mace)
            player.sendMessage("§aBaşarılı bir şekilde §2Gürz §asatın aldın!")
        },
        ShopItem("Mızrak\n", 100) { player ->
            val trident = Item.get(Item.TRIDENT)
            player.inventory.addItem(trident)
            player.sendMessage("§aBaşarılı bir şekilde §2Mızrak §asatın aldın!")
        },
    )
}