package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.Core
import redfox.skyblock.data.Mute
import redfox.skyblock.utils.Utils

class ReplyCommand : Command("r", "Sana son mesaj atan kişiye mesaj atar", "/r <mesaj>") {
    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        if (args.isEmpty()) {
            sender.sendMessage("§8» §cLütfen bir mesaj gir.")
            return false
        }

        val lastMessage = Utils.lastMessagePlayer[sender.name]

        if (lastMessage == null) {
            sender.sendMessage("§8» §cSana son mesaj atan bir oyuncu yok.")
            return false
        }

        if (sender is Player) {
            val senderUuid = sender.uniqueId.toString()
            if (Mute.isMuted(senderUuid)) {
                sender.sendMessage("§8» §cSusturulmuşsun, mesaj gönderemezsin.")
                return false
            }
        }

        val targetPlayer = Core.instance.server.getPlayerExact(lastMessage)

        if (targetPlayer == null || !targetPlayer.isOnline) {
            sender.sendMessage("§8» §c${lastMessage} oyuncusu çevrimiçi değil.")
            return false
        }

        val message = args.joinToString(" ")
        if (sender is Player) Utils.lastMessagePlayer[targetPlayer.name] = sender.name
        Utils.lastMessagePlayer[sender.name] = targetPlayer.name
        sender.sendMessage("§7§o${targetPlayer.name} kişisine fısıldıyorsunuz: $message")
        targetPlayer.sendMessage("§7§o$name size fısıldıyor: $message")
        return true
    }
}
