package redfox.skyblock.form.credi

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.custom.ElementDropdown
import cn.nukkit.form.element.custom.ElementInput
import cn.nukkit.form.window.CustomForm
import redfox.skyblock.data.Database
import redfox.skyblock.model.HistoryRecord
import redfox.skyblock.utils.Utils

object SendCrediForm {

    fun send(sender: Player, message: String = "") {
        val form = CustomForm("Kredi Gönder")

        val players = Server.getInstance().onlinePlayers.values
            .filter { it.name != sender.name }
            .map { it.name }
            .sorted()

        if (players.isEmpty()) {
            sender.sendMessage("§cŞu anda çevrimiçi başka oyuncu yok!")
            Utils.sound(sender, "item.trident.hit_ground")
            return
        }
        form.addElement(ElementLabel(message))
        form.addElement(ElementDropdown("§7Gönderilecek oyuncu", players))
        form.addElement(ElementInput("§7Gönderilecek kredi miktarı", "Örn: 100"))

        form.send(sender)

        form.onSubmit { _, response ->
            if (response == null) return@onSubmit

            val selectedIndex = response.getDropdownResponse(1).elementId()
            val targetName = players.getOrNull(selectedIndex)
            val amountStr = response.getInputResponse(2) ?: ""
            val amount = amountStr.toIntOrNull()

            if (targetName == null) {
                send(sender, "§cGeçerli bir oyuncu seçmedin!")
                Utils.sound(sender, "item.trident.hit_ground")
                return@onSubmit
            }

            if (amount == null || amount <= 0) {
                send(sender, "§cGeçerli bir miktar gir!")
                Utils.sound(sender, "item.trident.hit_ground")
                return@onSubmit
            }

            if (Server.getInstance().getPlayerExact(targetName) !is Player) {
                send(sender, "§c${targetName} adlı oyuncu oyunda değil!")
                Utils.sound(sender, "item.trident.hit_ground")
                return@onSubmit
            }

            if (!Database.hasCredi(sender.name, amount)) {
                send(sender, "§cYeterli kredin yok!")
                Utils.sound(sender, "item.trident.hit_ground")
                return@onSubmit
            }

            Database.removeCredi(sender.name, amount)
            Database.addCredi(targetName, amount)

            sender.sendMessage("§a$amount kredi §e$targetName§a oyuncusuna gönderildi.")

            Server.getInstance().getPlayerExact(targetName)?.sendMessage(
                "§a${sender.name} sana §e$amount§a kredi gönderdi!"
            )
            Utils.sound(sender, "note.harp")

            Database.addCrediRecord(
                sender.name,
                HistoryRecord(
                    name = sender.name,
                    action = "§cKredi Gönderildi§7 -> $targetName",
                    amount = amount
                )
            )

            Database.addCrediRecord(
                targetName,
                HistoryRecord(
                    name = targetName,
                    action = "§aKredi Alındı§7 <- ${sender.name}",
                    amount = amount
                )
            )
        }
    }
}