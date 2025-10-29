package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.item.Item
import cn.nukkit.item.enchantment.Enchantment
import redfox.skyblock.data.Database
import redfox.skyblock.form.kit.manager.KitManager

class BetaKitCommand : Command(
    "betakiti",
    "Beta kiti"
) {

    private val itemList = listOf(
        Item.get(Item.DIAMOND_SWORD).apply {
            addEnchantment(Enchantment.get(Enchantment.ID_DAMAGE_ALL)!!.setLevel(4))
            addEnchantment(Enchantment.get(Enchantment.ID_DURABILITY)!!.setLevel(3))
        },

        Item.get(Item.DIAMOND_PICKAXE).apply {
            addEnchantment(Enchantment.get(Enchantment.ID_EFFICIENCY)!!.setLevel(5))
            addEnchantment(Enchantment.get(Enchantment.ID_DURABILITY)!!.setLevel(3))
        },

        Item.get(Item.DIAMOND_HELMET).apply {
            addEnchantment(Enchantment.get(Enchantment.ID_PROTECTION_ALL)!!.setLevel(4))
            addEnchantment(Enchantment.get(Enchantment.ID_DURABILITY)!!.setLevel(3))
        },

        Item.get(Item.DIAMOND_CHESTPLATE).apply {
            addEnchantment(Enchantment.get(Enchantment.ID_PROTECTION_ALL)!!.setLevel(4))
            addEnchantment(Enchantment.get(Enchantment.ID_DURABILITY)!!.setLevel(3))
        },

        Item.get(Item.DIAMOND_LEGGINGS).apply {
            addEnchantment(Enchantment.get(Enchantment.ID_PROTECTION_ALL)!!.setLevel(4))
            addEnchantment(Enchantment.get(Enchantment.ID_DURABILITY)!!.setLevel(3))
        },

        Item.get(Item.DIAMOND_BOOTS).apply {
            addEnchantment(Enchantment.get(Enchantment.ID_PROTECTION_ALL)!!.setLevel(4))
            addEnchantment(Enchantment.get(Enchantment.ID_DURABILITY)!!.setLevel(3))
        },

        Item.get(Item.GOLDEN_APPLE, 0, 10),
        Item.get(Item.COOKED_BEEF, 0, 64)
    )


    override fun execute(sender: CommandSender?, commandLabel: String?, args: Array<out String>?): Boolean {
        if (sender !is Player) return false

        val kitName = "betaKit"

        if (!KitManager.canTakeKit(sender.name, kitName)) {
            val nextTime = Database.getLastKitTakenTime(sender.name, kitName) ?: 0L
            val now = System.currentTimeMillis() / 1000
            val remaining = nextTime + KitManager.cooldownSeconds - now

            val msg = if (remaining > 0) {
                "§cBu kiti tekrar alabilmek için §f${KitManager.formatTime(remaining)} §cbeklemelisin."
            } else {
                "§cBu kiti şu anda alamazsın."
            }
            sender.sendMessage(msg)
            return false
        }

        val testItems = itemList.map { it.clone() }.toTypedArray()
        val leftovers = sender.inventory.addItem(*testItems)

        if (leftovers.isNotEmpty()) {
            sender.sendMessage("§cEnvanterinde yeterli boşluk yok, kiti alamadın.")
            return false
        }

        KitManager.setKitTaken(sender.name, kitName)
        sender.sendMessage("§a$kitName kiti başarıyla verildi!")
        return true
    }

}
