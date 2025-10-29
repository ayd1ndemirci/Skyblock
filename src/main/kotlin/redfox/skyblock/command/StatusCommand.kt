package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.command.ConsoleCommandSender
import redfox.skyblock.form.general.StatusForm
import redfox.skyblock.group.GroupManager
import redfox.skyblock.utils.Utils

class StatusCommand : Command("durum", "Oyuncunun durumunu kaldır") {

    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        if (sender is Player && args.isEmpty()) {
            StatusForm.send(sender)
            Utils.sound(sender, "note.harp")
            return true
        }
        if (sender is ConsoleCommandSender || !sender.isOp) {
            sender.sendMessage("§8» §cBu komutu kullanmak için yetkin yok.")
            return false
        }
        if (args.size != 1) {
            sender.sendMessage("§8» §cKullanım: /status <oyuncu>")
            return false
        }

        val targetPlayer = sender.server.getPlayer(args[0])
        if (targetPlayer == null || !targetPlayer.isOnline) {
            sender.sendMessage("§8» §cOyuncu bulunamadı veya çevrimdışı.")
            return false
        }

        val key = targetPlayer.name.lowercase()
        if (Utils.statusPlayers.containsKey(key)) {
            Utils.statusPlayers.remove(key)
            val format = GroupManager.getPlayerGroup(targetPlayer).nameTagFormat
            val replacedFormat = format.replace("%nickname%", targetPlayer.name)
            targetPlayer.nameTag = replacedFormat
            sender.sendMessage("§8» §2${targetPlayer.name} §aadlı oyuncunun durumu kaldırıldı.")
        } else sender.sendMessage("§8» §4${targetPlayer.name} §cadlı oyuncunun bir durumu yok!")
        return true
    }
}