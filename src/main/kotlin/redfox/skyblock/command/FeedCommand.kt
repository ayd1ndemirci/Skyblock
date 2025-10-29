package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.permission.Permission
import redfox.skyblock.utils.Utils

class FeedCommand : Command(
    "feed",
    "Açlığını giderir",
    "/feed [oyuncu]"
) {

    init {
        permission = Permission.FEED_COMMAND
        permissionMessage = "§8» §cBu komutu §4§lVIP §r§cve üzerleri kullanabilir. VIP satın almak için /vipbilgi."
    }

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        if (!testPermission(sender)) return false

        val targetPlayer: Player = when {
            args.isEmpty() -> {
                if (sender !is Player) {
                    sender.sendMessage("§8» §cBir oyuncu belirtmelisin.")
                    return false
                }
                sender
            }

            else -> {
                if (!Utils.isAdmin(sender)) {
                    sender.sendMessage("§8» §cBu komutu sadece yöneticiler kullanabilir.")
                    return false
                }

                sender.server.getPlayer(args[0]) ?: run {
                    sender.sendMessage("§8» §cBelirtilen oyuncu çevrimiçi değil.")
                    return false
                }
            }
        }

        targetPlayer.foodData.food = targetPlayer.foodData.maxFood
        for (i in targetPlayer.foodData.food..targetPlayer.foodData.maxFood) {
            targetPlayer.foodData.food = i
            Thread.sleep(50)
        }
        targetPlayer.foodData.saturation = 20f
        Utils.sound(targetPlayer, "random.orb")

        if (sender != targetPlayer) {
            sender.sendMessage("§8» §2§o${targetPlayer.name} §r§aoyuncusunun açlığı giderildi.")
        } else {
            sender.sendMessage("§8» §6Açlığın giderildi, yine bekleriz.")
        }
        return true
    }
}