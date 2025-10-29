package redfox.skyblock.command

import cn.nukkit.Server
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.command.data.CommandParamType
import cn.nukkit.command.data.CommandParameter
import redfox.skyblock.data.PunishmentRecord
import redfox.skyblock.enums.PunishmentType
import redfox.skyblock.permission.Permission

class KickCommand : Command(
    "kick",
    "Sunucudan lavuk kickler"
) {

    init {
        permission = Permission.KICK_COMMAND
        permissionMessage = "§8» §cBu komutu kullanmak için 5 fırın ekmek yemen lazım"
        commandParameters.clear()
        commandParameters["default"] = arrayOf(
            CommandParameter.newType("player", true, CommandParamType.TARGET),
            CommandParameter.newType("reason", true, CommandParamType.STRING)
        )
    }

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
        if (!testPermission(sender)) return true

        if (args.size < 2) {
            sender.sendMessage("§8» §cKullanım: /kick <oyuncu> <sebep>")
            return true
        }

        val targetName = args[0]
        val reason = args.copyOfRange(1, args.size).joinToString(" ")
        val target = Server.getInstance().getPlayerExact(targetName)

        if (target == null) {
            sender.sendMessage("§8» §c$targetName adlı oyuncu çevrimdışı.")
            return true
        }

        val kickMessage = "§cSunucudan Atıldın\n\n§4Sebep: §c$reason"

        PunishmentRecord.addPunishment(target.name, reason, sender.name, System.currentTimeMillis(), PunishmentType.KICK)

        target.close(kickMessage)

        sender.sendMessage("§8» §g${targetName} §6sunucudan atıldı.")
        return true
    }
}