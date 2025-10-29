package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.permission.Permission
import redfox.skyblock.utils.Utils

class HealCommand : Command(
    "heal",
    "Canını yeniler",
    "/heal [oyuncu]"
) {
    init {
        permission = Permission.HEAL_COMMAND
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

        val maxHealth = targetPlayer.maxHealth
        val currentHealth = targetPlayer.health.toInt()

        for (i in currentHealth..maxHealth) {
            targetPlayer.health = i.toFloat()
            Thread.sleep(50)
        }

        if (sender != targetPlayer) {
            sender.sendMessage("§8» §2§o${targetPlayer.name} §r§acanı yenilendi.")
        } else {
            sender.sendMessage("§8» §6Canın yenilendi, yine bekleriz.")
        }
        return true
    }
}