package redfox.skyblock.command.group

import cn.nukkit.Server
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.data.Tag
import redfox.skyblock.group.GroupManager
import redfox.skyblock.group.GroupManager.getGroup

class GiveGroupCommand : Command("tagver", "Oyunculara etiket (grup) verir.") {

    init {
        permission = "nukkit.command.op.give"
        commandParameters.clear()
    }

    override fun execute(sender: CommandSender, string: String, args: Array<String>): Boolean {
        if (!testPermission(sender)) return false
        if (args.size < 2) {
            sender.sendMessage("§cKullanım: /tagver <etiket> <oyuncu>")
            return false
        }

        val group = getGroup(args[0])
        if (group == null) {
            sender.sendMessage("§8» §4${args[0]} §cadında bir etiket bulunamadı.")
            return false
        }

        var name = args[1]
        val player = Server.getInstance().getPlayer(args[1])
        if (player != null) name = player.name

        if (!Tag.profileExists(name)) {
            sender.sendMessage("§c${name} adlı oyuncunun verisi bulunamadı.")
            return false
        }

        if (!GroupManager.getPlayerGroups(name).contains(group)) {
            GroupManager.setPlayerGroup(name, group)
            sender.sendMessage("§8» §2${name} §aisimli oyuncuya §2${group.name} §atagı verildi.")
        } else {
            sender.sendMessage("§8» §4${group.name} §cadlı bir tag zaten mevcut.")
        }

        return true
    }
}
