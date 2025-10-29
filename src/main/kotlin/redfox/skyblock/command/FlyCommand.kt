package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.network.protocol.types.PlayerAbility
import redfox.skyblock.permission.Permission
import redfox.skyblock.utils.Utils

class FlyCommand : Command(
    "fly",
    "Uçmanıza yarar"
) {

    init {
        permission = Permission.FLY_COMMAND
        permissionMessage = "§8» §cBu komutu §4§lVIP §r§cve üzerleri kullanabilir. VIP satın almak için /vipbilgi."
    }

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§8» §cBu komut sadece oyun içinde kullanılabilir.")
            return true
        }

        if (!testPermission(sender) || !sender.isOp) {
            return true
        }

        if (args.isNotEmpty()) {
            if (!sender.isOp) {
                sender.allowFlight = !sender.allowFlight
                sender.adventureSettings.set(PlayerAbility.FLYING, sender.allowFlight)
                sender.adventureSettings.update()
                sender.sendMessage("§8» §aUçma modu " + if (sender.allowFlight) "§aaktif edildi." else "§akapatıldı.")
                Utils.sound(sender, "random.orb")
                return true
            }

            val target = sender.server.getPlayer(args[0])
            if (target == null) {
                sender.sendMessage("§8» §cOyuncu bulunamadı.")
                return true
            }

            target.allowFlight = !target.allowFlight
            target.adventureSettings.set(PlayerAbility.FLYING, target.allowFlight)
            target.adventureSettings.update()
            target.sendMessage("§8» §aUçma modun " + if (target.allowFlight) "§aaktif edildi." else "§akapatıldı.")
            sender.sendMessage("§8» §2§o${target.name}§r §aiçin uçma modu " + if (target.allowFlight) "§aaktif edildi" else "§akapatıldı")
            Utils.sound(target, "random.orb")
            Utils.sound(sender, "random.orb")
            return true
        }

        sender.allowFlight = !sender.allowFlight
        sender.adventureSettings.set(PlayerAbility.FLYING, sender.allowFlight)
        sender.adventureSettings.update()
        sender.sendMessage("§8» §aUçma modu " + if (sender.allowFlight) "§aaktif edildi." else "§akapatıldı.")
        Utils.sound(sender, "random.orb")
        return true
    }
}
