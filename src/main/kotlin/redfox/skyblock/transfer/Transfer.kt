package redfox.skyblock.transfer

import cn.nukkit.Player
import java.net.InetSocketAddress

object Transfer {

    const val HUB_IP: String = "213.123.431.22"

    fun goToHub(player: Player, server: String = "179.61.147.21", port: Int = 19133) {
        val addr = InetSocketAddress(server, port)
        player.transfer(addr)
    }
}