package redfox.skyblock.command

import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.command.data.CommandParamType
import cn.nukkit.command.data.CommandParameter
import redfox.skyblock.data.Database

class ShoutBanCommand : Command("haykirbanla") {
    init {
        description = "Bir oyuncuyu haykırma sisteminden banlar"
        usage = "/haykirbanla <isim>"
        permission = "shout.ban"
        commandParameters["default"] = arrayOf(
            CommandParameter.newType("player", true, CommandParamType.TARGET)
        )
    }

    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage("§8» §cBu komutu kullanmak için yetkin yok.")
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage("§8» §cKullanım: /haykirbanla <isim>")
            return true
        }

        val targetName = args[0]
        Database.banShout(targetName)
        sender.sendMessage("§8» §a$targetName adlı oyuncu haykırmadan banlandı.")
        return true
    }
}
