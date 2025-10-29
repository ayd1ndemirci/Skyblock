package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.data.Database
import redfox.skyblock.form.shout.ShoutForm
import redfox.skyblock.utils.ShoutUtil
import redfox.skyblock.utils.Utils

class ShoutCommand : Command(
    "shout",
    "Haykırma sistemi komutu",
    "/shout [player] [miktar]",
    arrayOf("haykir")
) {
    override fun execute(sender: CommandSender?, commandLabel: String?, args: Array<out String>?): Boolean {
        if (sender !is Player) return false

        if (args == null || args.isEmpty()) {
            if (sender.isOp) {
                ShoutForm.send(sender)
                Utils.sound(sender, "note.harp")
                return true
            }

            if (ShoutUtil.isOnCooldown()) {
                val remaining = ShoutUtil.getRemainingCooldown()
                sender.sendMessage("§8» §cHaykırma sistemi şu anda beklemede. $remaining saniye beklemen gerekiyor.")
                return true
            }

            ShoutForm.send(sender)
            Utils.sound(sender, "note.harp")
            return true
        }

        if (!sender.isOp) {
            sender.sendMessage("§8» §cBu komutu sadece ayd1ndemirci kullanabilir.")
            return true
        }

        val targetName = args.getOrNull(0)
        val amountStr = args.getOrNull(1)

        if (targetName.isNullOrBlank()) {
            sender.sendMessage("§8» §cGeçerli bir oyuncu adı gir.")
            return true
        }

        val amount = amountStr?.toIntOrNull()
        if (amount == null || amount <= 0) {
            sender.sendMessage("§8» §cGeçerli bir miktar gir.")
            return true
        }

        val currentRights = Database.getShoutRight(targetName)
        Database.setShoutRight(targetName, currentRights + amount)

        sender.sendMessage("§8» §a$targetName isimli oyuncuya $amount haykırma hakkı verildi.")
        return true
    }
}
