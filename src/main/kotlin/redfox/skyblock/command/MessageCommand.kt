package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.command.data.CommandParamType
import cn.nukkit.command.data.CommandParameter
import redfox.skyblock.data.Database
import redfox.skyblock.data.Mute
import redfox.skyblock.utils.Utils

class MessageCommand : Command("msg", "Belirtilen oyuncuya gizli mesaj atar", "/msg <oyuncu> <mesaj>") {

    init {
        commandParameters.clear()
        commandParameters["default"] = arrayOf(
            CommandParameter.newType("player", true, CommandParamType.TARGET),
            CommandParameter.newType("message", true, CommandParamType.MESSAGE),
        )
    }

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        if (args.size < 2) {
            sender.sendMessage("§8» §cLütfen bir oyuncu ve mesaj girin.")
            return false
        }

        val targetName = args[0]
        val targetPlayer = sender.server.getPlayer(targetName)
        if (targetPlayer == null || !targetPlayer.isOnline) {
            sender.sendMessage("§8» §c${targetName} oyuncusu çevrimiçi değil.")
            return false
        }

        if (!Database.isSettingEnabled(targetPlayer.name, "message")) {
            sender.sendMessage("§8» §c${targetName} adlı oyuncu özel mesaj alımını kapatmış.")
            return false
        }

        if (sender is Player) {
            val senderUuid = sender.uniqueId.toString()
            if (Mute.isMuted(senderUuid)) {
                sender.sendMessage("§8» §cSusturulmuşsun, mesaj gönderemezsin.")
                return false
            }
        }

        val message = args.drop(1).joinToString(" ")
        val name = if (sender is Player) sender.displayName else sender.name
        sender.sendMessage("§7§o${targetPlayer.name} kişisine fısıldıyorsunuz: $message")
        targetPlayer.sendMessage("§7§o$name size fısıldıyor: $message")
        if (sender is Player) Utils.lastMessagePlayer[targetPlayer.name] = sender.name
        Utils.lastMessagePlayer[sender.name] = targetPlayer.name

        return true
    }
}
