package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.permission.Permission

class SizeCommand : Command(
    "size",
    "Oyuncunun boyutunu değiştirir.",
    "/size <boyut>",
    arrayOf("boyut")
) {

    init {
        permission = Permission.SIZE_COMMAND
        permissionMessage =
            "§8» §cBu komutu §4§lVIP §r§cve üzeri oyuncular kullanabilir. VIP satın almak için /vipbilgi."
    }

    override fun execute(sender: CommandSender?, label: String?, args: Array<out String?>?): Boolean {
        val player = sender as? Player ?: return sender?.sendMessage("§8» §cBu komut sadece oyunda kullanılabilir.")
            .let { false }
        if (!testPermission(player)) return player.sendMessage(permissionMessage).let { false }
        if (args.isNullOrEmpty()) return player.sendMessage("§8» §cKullanım: /size <boyut>").let { false }

        val size = args[0]?.toFloatOrNull()
        if (size == null || size !in 0.5f..5.0f) return player.sendMessage("§8» §cBoyut 0.5 ile 5 arasında olmalıdır.")
            .let { false }

        val target =
            if (args.size > 1 && args[1] != null && (player.hasPermission(Permission.ADMIN_SIZE_COMMAND) || player.isOp)) {
                Server.getInstance().getPlayerExact(args[1]).also {
                    if (it == null) return player.sendMessage("§8» §cOyuncu bulunamadı.").let { false }
                }
            } else player

        target.setScale(size)
        if (target != player) target.sendMessage("§8» §aBoyutunuz §2$size §aolarak ayarlandı.")
        player.sendMessage("§8» §a${if (target == player) "Boyutunuz" else "${target.name} adlı oyuncunun boyutu"} §2$size §aolarak ayarlandı.")
        return true
    }
}
