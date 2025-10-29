package redfox.skyblock.utils

import cn.nukkit.Player
import redfox.skyblock.permission.Permission

object HomeUtil {
    fun getLimit(player: Player): Int {
        return when {
            player.hasPermission(Permission.MVIP) -> 10
            player.hasPermission(Permission.VIP_PLUS) -> 5
            player.hasPermission(Permission.VIP) -> 4
            else -> 2
        }
    }
}
