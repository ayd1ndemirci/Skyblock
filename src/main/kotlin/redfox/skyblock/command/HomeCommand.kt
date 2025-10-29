package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import com.mongodb.client.model.Filters
import redfox.skyblock.data.HomeDB
import redfox.skyblock.data.MongoDB
import redfox.skyblock.form.home.HomeForm

class HomeCommand : Command(
    "home",
    "Ev noktası ayarlama menüsü",
    "/home",
    arrayOf("ev")
) {
    override fun execute(sender: CommandSender?, commandLabel: String?, args: Array<out String?>?): Boolean {
        if (sender !is Player) return false

        val collection = MongoDB.db.getCollection("homes")
        val existing = collection.find(Filters.eq("name", sender.name)).first()
        if (existing == null) {
            HomeDB.createEmptyHomesDocument(sender.name)
        }
        HomeForm.send(sender)
        return true
    }
}
