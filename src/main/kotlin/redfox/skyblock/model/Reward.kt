package redfox.skyblock.model

import cn.nukkit.Player
import cn.nukkit.item.Item
import cn.nukkit.item.ItemID
import redfox.skyblock.data.Database

sealed class Reward {

    abstract fun giveTo(player: Player)

    data class ItemReward(
        val item: Item,
        val amount: Int = 1
    ) : Reward() {
        override fun giveTo(player: Player) {
            val clone = item.clone()
            clone.count = amount
            player.inventory.addItem(clone)
        }
    }

    data class MoneyReward(
        val amount: Int
    ) : Reward() {
        override fun giveTo(player: Player) {
            player.sendMessage("§e+$amount para hesabına eklendi.")
            Database.addMoney(player, amount)
        }
    }

    data class MessageReward(
        val message: String
    ) : Reward() {
        override fun giveTo(player: Player) {
            player.sendMessage(message)
        }
    }

    companion object {
        val STEAK = ItemReward(Item.get(ItemID.COOKED_BEEF), 16)
        val EXAMPLE_MONEY = MoneyReward(100)
        val EXAMPLE_MSG = MessageReward("§bBugünün hediyesiyle başarılar!")
    }
}
