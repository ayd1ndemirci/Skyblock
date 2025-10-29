package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.*
import cn.nukkit.command.data.*
import redfox.skyblock.data.Database
import redfox.skyblock.manager.TeleportRequestManager

class TpaCommand : Command("tpa", "Işınlanma isteği gönder") {
    init {
        commandParameters.clear()
        commandParameters["default"] = arrayOf(CommandParameter.newType("hedef", true, CommandParamType.TARGET))
    }

    override fun execute(sender: CommandSender, label: String, args: Array<out String>?): Boolean {
        val from = sender as? Player ?: return false
        val to = from.server.getPlayerExact(args?.getOrNull(0) ?: return from.error("§cKullanım: /tpa <oyuncu>"))
        if (from == to) return from.error("§8» §cKendine ışınlanamazsın.")
        if (!Database.isSettingEnabled(to.name, "message")) {
            from.sendMessage("§8» §c${to.name} adlı oyuncu ışınlanma isteklerini kapatmış.")
            return false
        }

        if (!TeleportRequestManager.send(from, to)) return true

        from.msg("§7${to.name} adlı oyuncuya istek gönderildi.")
        to.msg(
            "§2${from.name} §aadlı oyuncu sana ışınlanma isteği gönderdi.\n" +
                    "§6Kabul: §a/tpak  §cReddet: §c/tpar\n" +
                    "§630 saniye içinde işlem yapılmazsa otomatik reddedilir."
        )
        return true
    }

    private fun Player.msg(msg: String) = sendMessage("§8» $msg")
    private fun Player.error(msg: String): Boolean = msg(msg).let { true }
}