package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.manager.ServerManager
import redfox.skyblock.utils.Utils
import java.util.concurrent.TimeUnit

class RestartCommand : Command(
    "res",
    "Sunucunun yeniden başlatılmasına kalan süreyi gösterir"
) {

    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        val task = ServerManager.restartTask
        if (task == null) {
            sender.sendMessage("§8» §cRestart görevi aktif değil.")
            return true
        }
        if (args.isNotEmpty() && args[0].equals("res", ignoreCase = true)) {
            if (sender is Player && !sender.isOp) {
                sender.sendMessage("§8» §cBu komutu kullanmak için yetkin yok.")
                return true
            }
            ServerManager.forceRestartIn(5)
            sender.sendMessage("§8» §aSunucu yeniden başlatılıyor... (5 saniye)")
            return true
        }

        val remaining = task.getRemainingSeconds().toLong()
        val timeString = formatTime(remaining)
        sender.sendMessage("§8» §eSunucu yeniden başlatılmasına §6$timeString §r§ekaldı")
        Utils.sound(sender as Player, "note.pling")
        return true
    }

    private fun formatTime(secondsLeft: Long): String {
        if (secondsLeft <= 0) return "saliseler!"
        val days = TimeUnit.SECONDS.toDays(secondsLeft)
        val hours = TimeUnit.SECONDS.toHours(secondsLeft) % 24
        val minutes = TimeUnit.SECONDS.toMinutes(secondsLeft) % 60
        val seconds = secondsLeft % 60

        val parts = mutableListOf<String>()
        if (days > 0) parts.add("$days gün")
        if (hours > 0) parts.add("$hours saat")
        if (minutes > 0) parts.add("$minutes dakika")
        if (seconds > 0) parts.add("$seconds saniye")

        return parts.joinToString(", ")
    }
}