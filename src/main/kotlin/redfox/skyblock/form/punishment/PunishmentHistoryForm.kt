package redfox.skyblock.form.punishment

import cn.nukkit.Player
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.data.PunishmentRecord

object PunishmentHistoryForm {

    fun send(player: Player, target: String) {
        val history = PunishmentRecord.getHistory(target)
        val form = SimpleForm("$target Sicil Kaydı")

        if (history.isEmpty()) {
            form.addElement(ElementLabel("§cBu oyuncunun sicil kaydı bulunmamaktadır."))
            form.send(player)
            return
        }

        history.reversed().forEachIndexed { index, doc ->
            val type = doc.getString("type")
            val reason = doc.getString("reason")
            val created = doc.getString("created")
            val buttonText = "${type.lowercase().uppercase()} - $created\n$reason"
            form.addElement(ElementButton(buttonText))
        }

        form.onSubmit { _, response ->
            if (response == null) return@onSubmit

            val index = response.buttonId()
            if (index in history.indices) {
                PunishmentDetailForm.send(player, target, history.reversed()[index])
            }
        }

        form.send(player)
    }
}
