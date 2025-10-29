package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.math.Vector3

class CenterCommand : Command(
    "center",
    "Oyuncuyu bloğun ortasına yerleştirir"
) {

    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§8» §cBu komut sadece oyunda kullanılabilir.")
            return true
        }

        val blockX = sender.floorX.toDouble() + 0.5
        val blockY = sender.y
        val blockZ = sender.floorZ.toDouble() + 0.5

        val centeredLocation = Vector3(blockX, blockY, blockZ)
        sender.teleport(centeredLocation)
        return true
    }
}