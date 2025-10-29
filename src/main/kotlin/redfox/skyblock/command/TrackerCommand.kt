package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.command.data.CommandParamType
import cn.nukkit.command.data.CommandParameter
import cn.nukkit.utils.TextFormat
import com.mongodb.client.model.Filters
import org.bson.Document
import redfox.skyblock.data.Tracker
import redfox.skyblock.utils.Utils
import java.text.SimpleDateFormat
import java.util.*

class TrackerCommand : Command("tracker", "Oyuncu bilgilerini gösterir") {

    init {
        aliases = arrayOf("whois")
        commandParameters["default"] = arrayOf(
            CommandParameter.newType("oyuncu", true, CommandParamType.STRING)
        )
    }

    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            sender.sendMessage("§8» §c/tracker <oyuncu>")
            return true
        }

        val targetName = args[0]
        val uuid = Tracker.getUUIDByName(targetName)
        if (uuid == null) {
            sender.sendMessage("§8» §c$targetName adlı oyuncu veritabanında bulunamadı.")
            return true
        }

        val doc: Document = Tracker.collection.find(Filters.eq("uuid", uuid)).firstOrNull()
            ?: run {
                sender.sendMessage("§8» §cOyuncu kaydı bulunamadı.")
                return true
            }

        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale("tr", "TR"))
        val lastJoin = doc.getLong("lastJoin")
        val lastJoinStr = sdf.format(Date(lastJoin))

        val message = """
            ${TextFormat.GOLD}» Oyuncu Bilgileri
            §8- §eAd: §f${doc["name"]}
            §8- §eUUID: §f${doc["uuid"]}
            §8- §eIP: §f${doc["ip"]}
            §8- §eDevice ID: §f${doc["deviceId"]}
            §8- §eXUID: §f${doc["xuid"]}
            §8- §eCihaz Modeli: §f${doc["deviceModel"]}
            §8- §eDil: §f${doc["language"]}
            §8- §eUI Profili: §f${doc["uiProfile"]}
            §8- §eGiriş Türü: §f${doc["inputMode"]}
            §8- §eSon Giriş: §f$lastJoinStr
        """.trimIndent()

        sender.sendMessage(message)
        Utils.sound(sender as Player, "note.harp")
        return true
    }
}
