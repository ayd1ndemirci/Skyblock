package redfox.skyblock.command

import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.data.VIP

class ExtendAllVIPsCommand : Command("extendallvip", "Tüm VIP üyelik sürelerini uzatır") {

    init {
        permission = "vip.admin.commands"
        permissionMessage = "§cBu komutu kullanma yetkiniz yok!"
    }

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        if (!testPermission(sender)) {
            sender.sendMessage(permissionMessage)
            return false
        }

        if (args.isEmpty()) {
            sender.sendMessage("§8» §cKullanım: /extendallvip <uzatma_süresi_saniye>")
            return false
        }

        val extendSeconds = try {
            args[0].toLong().also {
                if (it <= 0) {
                    sender.sendMessage("§8» §cLütfen pozitif bir süre girin.")
                    return false
                }
            }
        } catch (e: NumberFormatException) {
            sender.sendMessage("§8» §cGeçersiz süre! Lütfen sayı girin.")
            return false
        }

        val allVIPs = VIP.getPlayers()

        if (allVIPs.isEmpty()) {
            sender.sendMessage("§8» §cHiç VIP kayıt bulunamadı.")
            return true
        }

        var count = 0

        allVIPs.forEach { vipRecord ->
            val player = vipRecord["player"] as? String ?: return@forEach
            val vipType = vipRecord["vipType"] as? String ?: return@forEach
            val currentTime = (vipRecord["time"] as? Number)?.toLong() ?: 0L

            val newTime = if (currentTime > System.currentTimeMillis() / 1000) {
                currentTime + extendSeconds
            } else {
                System.currentTimeMillis() / 1000 + extendSeconds
            }

            VIP.updateVIP(player, vipType, newTime.toInt())
            count++
        }

        sender.sendMessage("§8» §2$count §aoyuncunun VIP süresi başarıyla §2$extendSeconds §asaniye uzatıldı.")
        return true
    }
}
