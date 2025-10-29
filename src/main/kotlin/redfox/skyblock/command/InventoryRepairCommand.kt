package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.item.Item
import redfox.skyblock.form.general.InventoryRepairForm
import redfox.skyblock.permission.Permission
import redfox.skyblock.utils.Utils
import kotlin.math.floor

class InventoryRepairCommand : Command(
    "envtamir",
    "Envanterindeki tüm eşyaları tamir etme komutu.",
    "/envrepair"
) {

    init {
        permission = Permission.INVENTORY_REPAIR_COMMAND
        permissionMessage = "§8» §cBu komutu §4§lVIP §r§cve üzerleri kullanabilir. VIP satın almak için /vipbilgi."
    }

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false

        if (!testPermission(sender) || sender.isOp) return false

        val items = mutableListOf<Item>()
        var control = false

        for (content in sender.inventory.contents.values) {
            if (content.damage > 0) {
                items.add(content)
                control = true
            }
        }

        if (!control) {
            sender.sendMessage("§8» §cEnvanterinde tamir edilecek bir eşya bulunamadı.")
            return true
        }

        val playerName = sender.name

        if (Utils.isPlayerOnTheRepairList(playerName)) {
            InventoryRepairForm.send(sender, items)
        } else {
            val remainingTime = Utils.getRepairRemainingTime(playerName)
            val currentTime = System.currentTimeMillis() / 1000

            if (remainingTime > currentTime) {
                val secondsLeft = remainingTime - currentTime
                val minutes = floor(secondsLeft / 60.0).toInt()
                val remainingSeconds = (secondsLeft % 60).toInt()

                val timeString = if (minutes == 0) {
                    "$remainingSeconds saniye"
                } else {
                    "$minutes dakika $remainingSeconds saniye"
                }
                sender.sendMessage("§8» §cTekrardan bu komutu kullanmak için §4§o$timeString §r§csonra tekrardan kullanabilirsin.")
            } else {
                Utils.removePlayerRepairList(playerName)
                Utils.startRepairing(sender.name)
                InventoryRepairForm.send(sender, items)
                Utils.sound(sender, "note.harp")
            }
        }
        return true
    }
}