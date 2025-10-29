package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.utils.TextFormat
import redfox.skyblock.data.Mute
import redfox.skyblock.permission.Permission

class UnmuteCommand : Command("unmute", "Oyuncunun susturmasını kaldırır") {

    init {
        permission = Permission.MUTE_COMMAND
        permissionMessage = "${TextFormat.RED}Bu komutu kullanmaya yetkin yok!"
    }

    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        if (sender is Player && !sender.hasPermission(permission)) {
            sender.sendMessage(permissionMessage)
            return true
        }
        if (args.isEmpty()) {
            sender.sendMessage("${TextFormat.RED}Kullanım: /unmute <oyuncu>")
            return true
        }

        val targetName = args.joinToString(" ")

        if (!Mute.isMuted(targetName)) {
            sender.sendMessage("${TextFormat.RED}$targetName oyuncusu mute değil.")
            return true
        }

        Mute.unmute(targetName)
        sender.sendMessage("${TextFormat.GREEN}$targetName oyuncusunun mute'u kaldırıldı.")

        val target = Server.getInstance().getPlayerExact(targetName)
        target?.sendMessage("${TextFormat.GREEN}Mute'un kaldırıldı, artık sohbet edebilirsin!")

        return true
    }
}
