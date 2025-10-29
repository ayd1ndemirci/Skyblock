package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import com.mongodb.client.model.Filters
import redfox.skyblock.data.MongoDB
import redfox.skyblock.manager.VIPManager
import redfox.skyblock.permission.Permission

class VIPControlCommand : Command("vipcontrol") {

    init {
        permission = Permission.GIVE_VIP_COMMAND
        permissionMessage = VIPManager.PERMISSION_MESSAGE
    }

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        if (!testPermission(sender) || !sender.isOp) {
            sender.sendMessage(permissionMessage)
            return false
        }

        if (sender !is Player && args.isEmpty()) {
            sender.sendMessage("§8» §cBu komutu konsoldan kullanırken oyuncu adı belirtmelisiniz!")
            return true
        }

        val targetName = if (args.isNotEmpty()) args[0] else sender.name
        val collection = MongoDB.db.getCollection("vips")
        val playerDataList = collection.find(Filters.eq("player", targetName.lowercase())).toList()

        if (playerDataList.isEmpty()) {
            sender.sendMessage("§8» §c$targetName isimli oyuncunun VIP kaydı bulunamadı.")
            return true
        }

        sender.sendMessage("§8» §a$targetName isimli oyuncunun VIP bilgileri:")

        for (playerData in playerDataList) {
            val vipType = playerData.getString("vipType")
            val timeVal = playerData["time"]
            val timeLeft = when (timeVal) {
                is Long -> timeVal
                is Int -> timeVal.toLong()
                else -> 0L
            }

            sender.sendMessage("§eVIP Türü: §b$vipType")
            sender.sendMessage("§eKalan Süre: §b${VIPManager.formatTime(timeLeft)}")
        }


        return true
    }
}
