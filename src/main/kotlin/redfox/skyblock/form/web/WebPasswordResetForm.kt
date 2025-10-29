package redfox.skyblock.form.web

import cn.nukkit.Player
import cn.nukkit.form.window.ModalForm
import redfox.skyblock.data.Web
import redfox.skyblock.utils.Utils

object WebPasswordResetForm {

    fun send(player: Player) {
        ModalForm("Şifre Sıfırlama")
            .content("Şifreni sıfırlamak istediğine emin misin?")
            .text("Evet", "Hayır")
            .onYes { p ->
                val db = Web()
                val newPass = Utils.generatePassword()
                val updated =
                    db.updatePlayer(p.name, mapOf("password" to newPass, "updated_at" to System.currentTimeMillis()))
                if (updated) {
                    p.sendMessage("§aŞifren başarıyla sıfırlandı! Yeni şifren: §a$newPass")
                } else {
                    p.sendMessage("§cŞifre sıfırlanırken hata oluştu!")
                }
            }
            .onNo { p ->
                p.sendMessage("Şifre sıfırlama iptal edildi.")
            }
            .send(player)
    }
}
