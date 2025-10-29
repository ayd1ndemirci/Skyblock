package redfox.skyblock.command

import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import com.mongodb.client.model.Filters
import redfox.skyblock.data.MongoDB
import redfox.skyblock.manager.VIPManager
import redfox.skyblock.permission.Permission

class RemoveVIPCommand : Command("removevip") {

    init {
        permission = Permission.GIVE_VIP_COMMAND
        permissionMessage = VIPManager.PERMISSION_MESSAGE
    }

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        if (!testPermission(sender) || !sender.isOp) {
            sender.sendMessage(permissionMessage)
            return false
        }

        if (args.isEmpty()) {
            sender.sendMessage("§8» §cKullanım: /removevip <oyuncu_adı>")
            return true
        }

        val targetName = args[0].lowercase()
        val collection = MongoDB.db.getCollection("vips")

        val existingVIP = collection.find(Filters.eq("player", targetName)).firstOrNull()

        if (existingVIP == null) {
            sender.sendMessage("§8» §c$targetName isimli oyuncunun VIP kaydı bulunamadı.")
            return true
        }

        collection.deleteMany(Filters.eq("player", targetName))

        sender.sendMessage("§8» §a$targetName isimli oyuncunun VIP kaydı başarıyla silindi!")

        return true
    }
}
