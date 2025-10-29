package redfox.skyblock.form.shout

import cn.nukkit.Player
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.custom.ElementInput
import cn.nukkit.form.element.custom.ElementToggle
import cn.nukkit.form.window.CustomForm
import redfox.skyblock.data.Database
import redfox.skyblock.utils.ShoutUtil

object SendShoutForm {

    fun send(player: Player) {
        val form = CustomForm("Haykır")
        form.addElement(ElementLabel("§bHaykırma Hakkın: §f${Database.getShoutRight(player.name)}"))
        form.addElement(ElementInput("Haykırılacak mesajı gir", "örn. Savaş başladı!"))
        form.addElement(ElementToggle("Haykırma kurallarını onaylıyorum", false))
        form.send(player)

        form.onSubmit { _, response ->
            if (response == null) return@onSubmit

            val message = response.getInputResponse(1).trim()
            val isConfirmed = response.getToggleResponse(2)

            if (Database.isShoutBanned(player.name)) {
                player.sendMessage("§cHaykırma sisteminden banlandınız.")
                return@onSubmit
            }

            if (!isConfirmed) {
                player.sendMessage("§cHaykırma kurallarını kabul etmek zorundasın.")
                return@onSubmit
            }

            if (ShoutUtil.isOnCooldown()) {
                val remaining = ShoutUtil.getRemainingCooldown()
                player.sendMessage("§cHaykırma sistemi şu anda beklemede. Lütfen $remaining saniye sonra tekrar dene.")
                return@onSubmit
            }

            if (message.isEmpty()) {
                player.sendMessage("§cBoş mesaj gönderemezsin.")
                return@onSubmit
            }

            if (message.length < 5 || message.length > 100) {
                player.sendMessage("§cMesaj uzunluğu en az 5, en fazla 70 karakter olmalıdır.\n")
                return@onSubmit
            }
            val rights = Database.getShoutRight(player.name)
            if (rights < 1) {
                player.sendMessage("§cHaykırmak için yeterli hakkın yok.")
                return@onSubmit
            }

            player.server.broadcastMessage(
                "\n§7<---------- §a§lHAYKIR§r §7---------->\n" +
                        "\n\n§7*\n\n§6§l${player.name}§r: §g${message}\n\n§r§7*\n\n" +
                        "\n§7<---------- §a§lHAYKIR§r §7---------->\n"
            )

            ShoutUtil.playSoundToAll("custom.sound.scream")
            ShoutUtil.startCooldown()

            Database.setShoutRight(player.name, rights - 1)
        }
    }
}
