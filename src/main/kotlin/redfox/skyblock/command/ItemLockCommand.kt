package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.item.Item
import redfox.skyblock.form.general.ItemLockForm
import redfox.skyblock.permission.Permission

class ItemLockCommand : Command(
    "itemlock",
    "Eşyayı yanlışlıkla yere atma gibi derdin olmaz :)",
    "itemlock",
    arrayOf("esyakilit")
) {
    init {
        permission = Permission.ITEM_LOCK_COMMAND
        permissionMessage = "§8» §cBu komutu §4§lVIP §r§cve üzerleri kullanabilir. VIP satın almak için /vipbilgi."
    }

    override fun execute(sender: CommandSender?, commandLabel: String?, args: Array<out String?>?): Boolean {
        if (sender !is Player) return false
        if (!testPermission(sender) || !sender.isOp) return false

        val hand = sender.inventory.itemInHand
        if (!hand.isTool) {
            sender.sendMessage("§8» §cElinde bir alet olması gerekiyor.")
            return false
        }

        ItemLockForm.send(sender, hand)
        return true
    }
}