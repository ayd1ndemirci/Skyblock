package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.command.data.CommandParamType
import cn.nukkit.command.data.CommandParameter
import cn.nukkit.utils.TextFormat
import redfox.skyblock.data.Ban
import redfox.skyblock.data.PunishmentRecord
import redfox.skyblock.data.Tracker
import redfox.skyblock.enums.PunishmentType
import redfox.skyblock.permission.Permission
import redfox.skyblock.utils.AdminUtil
import java.text.SimpleDateFormat
import java.util.*

class BanCommand : Command("ban", "Oyuncu banlar :)") {

    init {
        permission = Permission.BAN_COMMAND
        permissionMessage = "${TextFormat.RED}Bu komutu kullanmaya yetkin yok!"
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
            sender.sendMessage("§8» ${TextFormat.RED}Kullanım: /ban <oyuncu> <sebep> <süre>")
            return true
        }

        val durationStr = args.last()
        val argsWithoutDuration = args.dropLast(1)

        val playerName = argsWithoutDuration[0].lowercase()
        val reason = argsWithoutDuration.drop(1).joinToString(" ")
        val bannedBy = if (sender is Player) sender.name else "Konsol"

        val until: Long
        val formattedEnd: String

        if (durationStr.equals("sınırsız", ignoreCase = true)) {
            until = Long.MAX_VALUE
            formattedEnd = "SINIRSIZ"
        } else {
            val durationMs = AdminUtil.parseDuration(durationStr)
            if (durationMs <= 0) {
                sender.sendMessage("§8» §cGeçersiz süre formatı! (örn: 15g, 10d, 1s, 2a veya sınırsız)")
                return true
            }
            until = System.currentTimeMillis() + durationMs
            val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm")
            formattedEnd = formatter.format(Date(until))
        }


        Ban.setBan(playerName, reason, bannedBy, until)
        PunishmentRecord.addPunishment(playerName, reason, bannedBy, until, PunishmentType.BAN)


        sender.sendMessage("§8» §2§o$playerName §r§abaşarıyla banlandı.")

        val target = Server.getInstance().getPlayerExact(playerName)
        if (target != null) {
            Tracker.trackPlayer(target)
            target.close("§c§lSunucudan banlandınız!§r\n\n§7Sebep: §f$reason\n§7Süre: §f$formattedEnd")
        }

        Server.getInstance().broadcastMessage(
            """
            ${AdminUtil.BAN_TITLE}
            §8» §eOyuncu: §6$playerName
            §8» §eYetkili: §6$bannedBy
            §8» §eSebep: §6$reason
            §8» §eBan bitiş tarihi: §6$formattedEnd
            ${AdminUtil.BAN_TITLE}
            """.trimIndent()
        )

        return true
    }
}
