package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.utils.Utils

class PlayTimeCommand : Command(
    "playtime",
    "Oyundaki aktiflik sürenizi gösterir.",
    "/playtime",
    arrayOf("oynamasurem")
) {

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§8» Bu komut sadece oyuncular tarafından kullanılabilir.")
            return false
        }

        if (args.isEmpty()) {
            val timeString = Utils.getPlayTimeConverter(sender.name)
            sender.sendMessage("§8» §6Toplam Aktiflik Süren: §g$timeString §r\n§7Gerçek süreyi öğrenmek için sunucudan ayrılıp tekrar girin.")
        } else {
            if (!sender.isOp) {
                sender.sendMessage("§8» §cBu komutu başka oyuncuların süresini görmek için kullanamazsınız. (Admin değilsin :d)")
                return false
            }

            val targetName = args[0]
            val timeString = Utils.getPlayTimeConverter(targetName)
            sender.sendMessage("§8» §6$targetName adlı oyuncunun aktiflik süresi: §g$timeString")
        }
        Utils.sound(sender, "note.pling")

        return true
    }
}
