package redfox.skyblock.form.settings

import cn.nukkit.Player
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.simple.ButtonImage
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.window.SimpleForm

object ChangedSettingsForm {

    private val keyToReadableName = mapOf(
        "message" to "Mesaj İstekleri",
        "tpa" to "Işınlanma İstekleri",
        "gift" to "Hediye Alımı",
        "friend" to "Arkadaşlık İstekleri",
        "notices" to "Bilgilendirme Mesajları",
        "autoinv" to "Otomatik Envanter",
        "durability" to "Eşya Can Göstergesi"
    )

    fun send(player: Player, changes: Map<String, Pair<Boolean, Boolean>>) {
        val form = SimpleForm("Ayarlar Menüsü")

        val contentBuilder = StringBuilder()
        contentBuilder.append("§aAşağıdaki ayarlar değiştirildi (${changes.size}):\n\n")
        for ((key, value) in changes) {
            val readableKey = keyToReadableName[key] ?: key
            val new = if (value.second) "§aaçıldı" else "§ckapatıldı"
            contentBuilder.append("§7- §e$readableKey $new\n")
        }
        form.addElement(ElementLabel(contentBuilder.toString()))
        form.addElement(
            ElementButton(
                "Kapat",
                ButtonImage(ButtonImage.Type.PATH, "textures/ui/close_button_default.png")
            )
        )
        form.send(player)
    }
}
