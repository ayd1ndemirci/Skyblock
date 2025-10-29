package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.form.general.ClearForm
import redfox.skyblock.utils.Utils

class ClearCommand : Command("clear", "Envanteri temizler", "/clear [oyuncu]") {
    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        var targetPlayer: Player?

        if (Utils.isAdmin(sender)) {
            if (sender !is Player && args.isEmpty()) {
                sender.sendMessage("§8» §cLütfen bir oyuncu belirtin.")
                return false
            }

            if (args.isNotEmpty()) {
                val targetName = args[0]
                targetPlayer = sender.server.getPlayer(targetName)
                if (targetPlayer == null) {
                    sender.sendMessage("§8» §cBelirtilen oyuncu çevrimiçi değil.")
                    return false
                }
            } else targetPlayer = sender as Player
        } else targetPlayer = sender as Player

        if (args.isEmpty() || !Utils.isAdmin(sender) || sender == targetPlayer) {
            ClearForm.send(targetPlayer)
            return true
        }

        val inventory = targetPlayer.inventory
        inventory.clearAll()
        if (sender != targetPlayer) {
            sender.sendMessage("§8» §2§o${targetPlayer.name} §r§aoyuncusunun envanteri başarıyla temizlendi.")
        }

        targetPlayer.sendMessage("§aEnvanteriniz başarıyla temizlendi.")
        return true
    }
}
