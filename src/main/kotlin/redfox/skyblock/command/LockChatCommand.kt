package redfox.skyblock.command

import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.command.ConsoleCommandSender
import redfox.skyblock.permission.Permission
import redfox.skyblock.utils.Utils

class LockChatCommand : Command(
    "sohbetkilit",
    "Sohbeti kilitler",
    "/sohbetkilit",
    arrayOf("lc")
) {

    init {
        permission = Permission.CHAT_LOCK_COMMAND
        permissionMessage = "§cBu komutu kullanamazsın."
    }

    override fun execute(sender: CommandSender?, commandLabel: String?, args: Array<out String?>?): Boolean {
        if (sender == null) return false

        if (sender !is ConsoleCommandSender && !sender.hasPermission(permission) || !sender.isOp) {
            sender.sendMessage(permissionMessage)
            return true
        }

        Utils.chatLocked = !Utils.chatLocked
        val message =
            if (Utils.chatLocked) "§cSohbet geçici süreliğine §4'Yönetici' §r§ctarafından kilitlendi." else "§aSohbet kiliti açıldı."
        sender.server.broadcastMessage(message)
        sender.server.levels.values.forEach { level ->
            level.players.values.forEach { onlinePlayers ->
                Utils.sound(onlinePlayers, "item.trident.hit_ground")
            }
        }
        return true
    }
}