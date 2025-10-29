package redfox.skyblock.manager

import cn.nukkit.Player

data class ShopItem(
    val name: String,
    val cost: Int,
    val action: (Player) -> Unit
)