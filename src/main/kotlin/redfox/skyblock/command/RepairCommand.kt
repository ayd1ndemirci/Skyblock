package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.permission.Permission
import redfox.skyblock.utils.Utils

class RepairCommand : Command("tamir", "Elindeki eşyayı tamir eder", "/tamir") {

    private val repairFullPrice = 1000
    private val cooldownTimeMillis = 10_000L

    // Basit cooldown map (player UUID -> son tamir zamanı millis)
    private val cooldowns = mutableMapOf<String, Long>()

    init {
        permission = Permission.REPAIR_COMMAND
        permissionMessage = "§8» §cBu komutu §4§lVIP §r§cve üzerleri kullanabilir. VIP satın almak için /vipbilgi."

    }
    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§cBu komutu sadece oyuncular kullanabilir.")
            return false
        }

        if (!testPermission(sender)) return false

        if (args.isNotEmpty()) {
            sender.sendMessage("§cBaşka oyuncuların eşyalarını tamir edemezsin.")
            return false
        }

        val now = System.currentTimeMillis()
        val lastUse = cooldowns[sender.uniqueId.toString()] ?: 0L
        val timeLeft = cooldownTimeMillis - (now - lastUse)

        if (timeLeft > 0) {
            sender.sendMessage("§cBu komutu tekrar kullanmak için ${timeLeft / 1000} saniye beklemelisin.")
            return false
        }

        val item = sender.inventory.itemInHand
        if (item.isNull) {
            sender.sendMessage("§cElinde tamir edilecek eşya yok.")
            Utils.sound(sender, "note.harp")
            return false
        }

        if (item.isUnbreakable) {
            sender.sendMessage("§cBu eşya tamir edilemez çünkü kırılmaz.")
            Utils.sound(sender, "note.harp")
            return false
        }

        if (item.damage == 0) {
            sender.sendMessage("§cEşya hasar görmemiş, tamire gerek yok.")
            Utils.sound(sender, "note.harp")
            return false
        }

        if (item.count > 1) {
            sender.close("§cHata Kodu: 418")
            return false
        }

        item.damage = 0
        sender.inventory.itemInHand = item
        sender.sendMessage("§8» §aElinde eşyayı tamir ettin.")
        Utils.sound(sender, "random.anvil_use")

        cooldowns[sender.uniqueId.toString()] = now

        return true
    }
}
