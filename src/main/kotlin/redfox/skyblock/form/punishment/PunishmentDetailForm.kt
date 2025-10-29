package redfox.skyblock.form.punishment

import cn.nukkit.Player
import cn.nukkit.form.window.ModalForm
import org.bson.Document
import redfox.skyblock.form.punishment.PunishmentHistoryForm

object PunishmentDetailForm {

    fun send(player: Player, target: String, doc: Document) {
        val type = doc.getString("type")
        val reason = doc.getString("reason")
        val created = doc.getString("created")
        val until = doc.getString("until") ?: "Süresiz"
        val issuedBy = doc.getString("issuedBy") ?: "Bilinmiyor"

        val content = buildString {
            append("§cİşlem Türü: §4$type\n")
            append("§cSebep: §4$reason\n")
            append("§cTarih: §4$created\n")
            append("§cBitiş: §4$until\n")
            append("§cYetkili: §4$issuedBy\n")
        }

        val form = ModalForm("Sicil Kaydı")
            .content(content)
            .text("§cGeri", "Kapat")
            .onYes { p ->
                PunishmentHistoryForm.send(p, target)
            }
            .send(player)
    }
}
