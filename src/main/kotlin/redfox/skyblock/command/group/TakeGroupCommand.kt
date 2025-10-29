package redfox.skyblock.command.group

import cn.nukkit.Server
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.data.Tag
import redfox.skyblock.group.GroupManager
import redfox.skyblock.group.GroupManager.getGroup

class TakeGroupCommand : Command("tagal", "Belirli bir oyuncudan grup (etiket) alır.") {

    init {
        permission = "nukkit.command.op.give"
        commandParameters.clear()
    }

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        if (!testPermission(sender)) return false
        if (args.size < 2) {
            sender.sendMessage("§cKullanım: /tagal <grup> <oyuncu>")
            return false
        }

        val group = getGroup(args[0])
        if (group == null) {
            sender.sendMessage("§cBelirtilen '${args[0]}' grubuna ait bir etiket bulunamadı.")
            return false
        }

        var name = args[1]
        val player = Server.getInstance().getPlayer(args[1])
        if (player != null) name = player.name

        if (!Tag.profileExists(name)) {
            sender.sendMessage("§c${name} adlı oyuncunun verisi bulunamadı.")
            return false
        }

        if (GroupManager.getPlayerGroups(name).contains(group)) {
            GroupManager.removePlayerGroup(name, group)
            sender.sendMessage("§a${name} adlı oyuncudan '${group.name}' etiketi başarıyla kaldırıldı.")
        } else {
            sender.sendMessage("§c${name} adlı oyuncuda '${group.name}' etiketi bulunamadı.")
        }

        return true
    }
}
