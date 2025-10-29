package redfox.skyblock.form.settings

import cn.nukkit.Player
import cn.nukkit.form.element.custom.ElementToggle
import cn.nukkit.form.window.CustomForm
import redfox.skyblock.data.Database
import redfox.skyblock.utils.Utils

object SettingsForm {

    fun send(player: Player) {
        val currentSettings = Database.getAllSettings(player.name)

        val form = CustomForm("Ayarlar Menüsü")
            .onSubmit { p, response ->
                val changedSettings = mutableMapOf<String, Pair<Boolean, Boolean>>()
                val keys = listOf("message", "tpa", "gift", "friend", "notices", "autoinv", "durability")

                for ((index, key) in keys.withIndex()) {
                    val newValue = response.getToggleResponse(index)
                    val oldValue = currentSettings[key] ?: true
                    if (newValue != oldValue) {
                        Database.setSetting(p.name, key, newValue)
                        changedSettings[key] = Pair(oldValue, newValue)
                    }
                }

                if (changedSettings.isNotEmpty()) {
                    ChangedSettingsForm.send(p, changedSettings)
                    Utils.sound(player, "note.harp")
                }
            }

        form.addElement(ElementToggle("Özel Mesaj İstekleri\n", currentSettings["message"] ?: true))
        form.addElement(ElementToggle("Işınlanma İstekleri\n", currentSettings["tpa"] ?: true))
        form.addElement(ElementToggle("Hediye Alımı\n", currentSettings["gift"] ?: true))
        form.addElement(ElementToggle("Arkadaşlık İstekleri\n", currentSettings["friend"] ?: true))
        form.addElement(ElementToggle("Bilgilendirme Mesajları\n", currentSettings["notices"] ?: true))
        form.addElement(ElementToggle("Otomatik Envanter\n", currentSettings["autoinv"] ?: true))
        form.addElement(ElementToggle("Eşya Can Göstergesi\n", currentSettings["durability"] ?: true))

        form.send(player)
    }
}
