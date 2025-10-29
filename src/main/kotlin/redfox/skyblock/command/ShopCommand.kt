package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.form.shop.ShopForm
import redfox.skyblock.utils.Utils

class ShopCommand : Command(
    "market",
    "Eşyaya mı ihtiyacın var? Kullan bukomutu",
    "/market",
    arrayOf("shop")
) {

    override fun execute(sender: CommandSender?, commandLabel: String?, args: Array<out String?>?): Boolean {
        if (sender !is Player) return false
        ShopForm.send(sender)
        Utils.sound(sender, "note.harp")
        return true
    }
}