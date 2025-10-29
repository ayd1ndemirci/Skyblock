package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.command.data.CommandParamType
import cn.nukkit.command.data.CommandParameter
import redfox.skyblock.data.Mute
import redfox.skyblock.data.PunishmentRecord
import redfox.skyblock.enums.PunishmentType
import redfox.skyblock.utils.AdminUtil
import java.text.SimpleDateFormat
import java.util.*

class MuteCommand : Command("mute", "Oyuncuyu sustur") {

    init {
        commandParameters["default"] = arrayOf(
            CommandParameter.newType("oyuncu", true, CommandParamType.TARGET),
            CommandParameter.newType("sebep", true, CommandParamType.STRING),
            CommandParameter.newType("süre", true, CommandParamType.STRING)
        )
    }

    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        if (sender is Player && !sender.hasPermission(permission)) {
            sender.sendMessage(permissionMessage)
            return true
        }
        if (args.size < 3) {
            sender.sendMessage("§8» §cKullanım: /mute <oyuncu> <sebep> <süre>")
            return true
        }

        val durationStr = args.last()
        val durationMs = AdminUtil.parseDuration(durationStr)
        if (durationMs <= 0) {
            sender.sendMessage("§8» §cGeçersiz süre formatı! (örn: 15g, 10d, 1s, 2a)")
            return true
        }

        val argsWithoutDuration = args.dropLast(1)

        val playerName = argsWithoutDuration[0].lowercase()
        val reason = argsWithoutDuration.drop(1).joinToString(" ")

        val until = System.currentTimeMillis() + durationMs
        val mutedBy = if (sender is Player) sender.name else "Konsol"

        Mute.setMute(playerName, reason, mutedBy, until)
        PunishmentRecord.addPunishment(playerName, reason, mutedBy, until, PunishmentType.MUTE)

        val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm")
        val formattedEnd = formatter.format(Date(until))

        sender.sendMessage("§8» §2§o$playerName §r§abaşarıyla susturuldu.")

        val target = Server.getInstance().getPlayerExact(playerName)
        target?.sendMessage("§8» §cSohbeti §4'$reason' §csebebi ile §4$formattedEnd §ctarihine kadar kullanamazsın")

        return true
    }
}

