package redfox.skyblock.command

import cn.nukkit.Server
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender

class AnnouncementCommand : Command(
    "duyuru",
    "Duyur babuş duyurr"
) {
    override fun execute(sender: CommandSender?, commandLabel: String?, args: Array<out String?>?): Boolean {
        if (!testPermission(sender)) return sender?.sendMessage("§8» §cBu komutu kullanamazsın.").let { false }

        if (args.isNullOrEmpty()) return sender?.sendMessage("§8» §cKullanım: /duyuru <mesaj>").let { false }

        val message = args.filterNotNull().joinToString(" ")
        Server.getInstance().broadcastMessage(
            """
            §7-*-*-*-*-*-*-*-*-*-* §c§lDUYURU§r §7-*-*-*-*-*-*-*-*-*-*
            §7*
            §c${sender?.name}: §f$message
            §7*
            §7-*-*-*-*-*-*-*-*-*-* §c§lDUYURU§r §7-*-*-*-*-*-*-*-*-*-*
            """.trimIndent()
        )
        return true
    }
}