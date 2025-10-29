package redfox.skyblock.event

import cn.nukkit.event.Listener

class AutoSellListener : Listener {
    /*
        private val sellableBlocks = listOf(
            SellableItem("lapis", BlockID.LAPIS_ORE, ItemID.LAPIS_LAZULI, 190),
            SellableItem("taş", BlockID.STONE, BlockID.STONE, 5),
            SellableItem("kum", BlockID.SAND, BlockID.SAND, 8)
        )

        @EventHandler
        fun onBlockBreak(event: BlockBreakEvent) {
            val player = event.player
            val block = event.block
            val inv = player.inventory

            for (sellable in sellableBlocks) {
                if (block.id == sellable.blockId && AutoSell.get(player.name, sellable.key)) {
                    val item = Item.get(sellable.itemId, sellable.itemDamage)
                    var totalAmount = 0

                    for (invItem in inv.contents.values) {
                        if (invItem.id == item.id && invItem.damage == item.damage) {
                            totalAmount += invItem.count
                        }
                    }

                    if (totalAmount >= 64) {
                        var toRemove = 64
                        for ((slot, invItem) in inv.contents) {
                            if (invItem.id == item.id && invItem.damage == item.damage) {
                                val removeCount = minOf(invItem.count, toRemove)
                                val newCount = invItem.count - removeCount
                                if (newCount <= 0) {
                                    inv.clear(slot)
                                } else {
                                    inv.setItem(slot, invItem.clone().apply { count = newCount })
                                }
                                toRemove -= removeCount
                                if (toRemove <= 0) break
                            }
                        }

                        Money.add(player, sellable.price)
                        player.sendActionBar("§b64x ${sellable.displayName} §eotomatik satıldı §f- §b${sellable.price} RF")
                    }
                }
            }
        }

        data class SellableItem(
            val key: String,
            val blockId: String,
            val itemId: String,
            val price: Int,
            val itemDamage: Int = 0,
            val displayName: String = key.replaceFirstChar { it.uppercase() }
        )*/
}
