package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.command.data.CommandParamType
import cn.nukkit.command.data.CommandParameter
import redfox.skyblock.utils.Utils

class PingCommand : Command(
    "ping",
    "Sunucu ile arandaki gecikmeyi gösterir",
    "/ping [oyuncu]"
) {
    init {
        commandParameters.clear()
        commandParameters["default"] = arrayOf(
            CommandParameter.newType("player", true, CommandParamType.TARGET),
        )
    }

    override fun execute(sender: CommandSender?, commandLabel: String?, args: Array<out String>?): Boolean {
        if (sender !is Player) {
            sender?.sendMessage("§cBu komutu sadece oyuncular kullanabilir.")
            return false
        }

        if (args.isNullOrEmpty()) {
            sender.sendMessage("§8» §dGecikmen: §5${sender.ping}ms")
            return true
        }

        val targetName = args[0]
        val target = Server.getInstance().getPlayerExact(targetName)

        if (target == null || !target.isOnline) {
            sender.sendMessage("§cOyuncu bulunamadı veya çevrimdışı.")
            return true
        }

        sender.sendMessage("§8» §5${target.name} §doyuncusunun gecikmesi: §5${target.ping}ms")
        Utils.sound(sender, "note.pling")
        return true
    }
}
