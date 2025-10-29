package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.data.Web
import redfox.skyblock.form.web.WebForm
import redfox.skyblock.utils.Utils

class WebCommand : Command(
    "web",
    "Web komutunu görürsün",
    "/web"
) {


    override fun execute(sender: CommandSender, label: String, args: Array<String>): Boolean {
        if (sender !is Player) return true

        val db = Web()

        if (db.getPlayer(sender.name) == null) {
            db.addPlayer(
                sender.name,
                Utils.generatePassword(),
                "Oyuncu",
                0,
                false,
                System.currentTimeMillis(),
                System.currentTimeMillis()
            )
        }

        WebForm.send(sender)
        return true
    }
}
