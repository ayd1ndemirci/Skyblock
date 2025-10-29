package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import com.mongodb.client.model.Filters
import redfox.skyblock.data.MongoDB
import redfox.skyblock.form.general.VIPForm
import redfox.skyblock.utils.Utils

class VIPCommand : Command("vipsurem") {

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        if (sender !is Player) return false

        val player = sender
        val collection = MongoDB.db.getCollection("vips")

        val playerVIPs = collection.find(Filters.eq("player", player.name.lowercase())).toList()

        if (playerVIPs.isEmpty()) {
            player.sendMessage("§8» §cVIP üyeliğiniz bulunmamaktadır.")
            return true
        }

        VIPForm.send(player)
        Utils.sound(sender, "note.harp")
        return true
    }
}
