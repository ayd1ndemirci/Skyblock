package redfox.skyblock.form.general

import cn.nukkit.Player
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.custom.ElementDropdown
import cn.nukkit.form.window.CustomForm
import redfox.skyblock.data.Database
import redfox.skyblock.utils.Utils

object SetJoinMessageForm {

    private val joinMessages = listOf(
        "oyuna katıldı!",
        "gizemleri çözmeye geldi!",
        "bir yıldız gibi kaydı!",
        "uzaydan iniş yaptı!",
        "sınırları aşmaya geldi!",
        "krallığı ele geçirmeye geldi!",
        "kılıçlarınızı bileyin, oyunun kralı geldi!",
        "FBI, opın dı dOoR!",
        "diyor ki: sırları açığa çıkarma zamanı!",
        "diyor ki: gökyüzünde kükreyen bir yıldırım gibiyim!",
        "diyor ki: ALT + F4 atmaya hazır olun!"
    )

    fun send(player: Player) {
        if (!Database.hasJoinMessage(player.name)) {
            Database.setJoinMessage(player.name, joinMessages[0])
        }

        val current = Database.getJoinMessage(player.name) ?: joinMessages[0]
        val capitalizedList = joinMessages.map { it.replaceFirstChar(Char::uppercaseChar) }

        val form = CustomForm("Giriş Mesajını Ayarla")
        form.addElement(ElementLabel("§6Giriş Mesajın: §g${current.replaceFirstChar(Char::uppercaseChar)}"))
        form.addElement(ElementDropdown("Giriş Mesajı Seç:", capitalizedList))

        form.send(player)

        form.onSubmit { _, response ->
            if (response == null) return@onSubmit

            val selectedIndex = response.getDropdownResponse(1).elementId()
            val selectedMessage = joinMessages[selectedIndex]
            val currentMessage = Database.getJoinMessage(player.name)

            if (currentMessage == selectedMessage) {
                player.sendMessage("§cGiriş mesajın zaten bu!")
                return@onSubmit
            }

            Database.updateJoinMessage(player.name, selectedMessage)
            player.sendMessage("§6Giriş mesajın §e'$selectedMessage' §6olarak ayarlandı.")
            Utils.sound(player, "random.orb")
        }
    }
}
