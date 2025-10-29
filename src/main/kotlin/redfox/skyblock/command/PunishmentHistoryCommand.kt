package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.command.data.CommandParamType
import cn.nukkit.command.data.CommandParameter
import redfox.skyblock.data.Database
import redfox.skyblock.form.punishment.PunishmentHistoryForm
import redfox.skyblock.permission.Permission

class PunishmentHistoryCommand : Command(
    "sicil",
    "Oyuncunun önceki sicilini görüntüler"
) {

    init {
        permission = Permission.PUNISHMENT_HISTORY_COMMAND
        permissionMessage = "§cBu komutu yetkililer kullanabilir."
        commandParameters.clear()
        commandParameters["default"] = arrayOf(
            CommandParameter.newType("player", true, CommandParamType.TARGET),
        )
    }

    override fun execute(sender: CommandSender?, commandLabel: String?, args: Array<out String?>?): Boolean {
        if (sender !is Player) return false
        if (!testPermission(sender)) return false

        val target = args?.getOrNull(0)?.lowercase()
        if (target.isNullOrBlank()) {
            sender.sendMessage("§cLütfen bir oyuncu adı girin. Örnek: /sicil <oyuncu>")
            return false
        }

        if (Database.get(target) == null) {
            sender.sendMessage("§cBu oyuncu veri tabanında bulunamadı. Muhtemelen hiç giriş yapmamış.")
            return false
        }

        PunishmentHistoryForm.send(sender, target)
        return true
    }
}
