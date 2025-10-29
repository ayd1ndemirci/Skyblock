package redfox.skyblock.command

import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.data.VIP
import redfox.skyblock.manager.VIPManager
import redfox.skyblock.permission.Permission

class GiveVIPCommand : Command(
    "vipver",
    "VIP verme komutu"
) {

    init {
        aliases = arrayOf("givevip")
        permission = Permission.GIVE_VIP_COMMAND
        permissionMessage = VIPManager.PERMISSION_MESSAGE
    }

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        if (!testPermission(sender) || !sender.isOp) {
            sender.sendMessage(permissionMessage)
            return false
        }

        if (args.size < 3) {
            sender.sendMessage("§8» §cKullanım: /vipver <oyuncu> <vip> <gün>")
            return false
        }

        val playerName = args[0]
        val vipType = args[1]
        val daysStr = args[2]

        val validVIPs = VIPManager.getVIPTypes()

        if (!validVIPs.contains(vipType)) {
            sender.sendMessage("§8» §cGeçersiz VIP türü! Geçerli VIP'ler: ${validVIPs.joinToString(", ")}")
            return false
        }

        val days = try {
            daysStr.toInt().also {
                if (it <= 0) {
                    sender.sendMessage("§8» §cGün sayısı pozitif bir sayı olmalıdır.")
                    return false
                }
            }
        } catch (e: NumberFormatException) {
            sender.sendMessage("§8» §cGeçersiz gün sayısı! Lütfen bir sayı girin.")
            return false
        }

        sender.sendMessage("§8» §a$playerName adlı oyuncuya $vipType verildi! ($days gün)")
        VIP.addVIP(playerName, vipType, days)
        return true
    }
}
