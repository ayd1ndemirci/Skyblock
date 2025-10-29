package redfox.skyblock.form.web

import cn.nukkit.Player
import cn.nukkit.form.window.ModalForm
import redfox.skyblock.data.Web

object WebPasswordForm {

    fun send(player: Player) {
        val db = Web()
        val playerData = db.getPlayer(player.name) ?: run {
            player.sendMessage("§cVeri bulunamadı!")
            return
        }

        val password = playerData.getString("password")

        ModalForm("Şifren")
            .content("Şifren: §a$password")
            .text("§c<- Geri", "Tamam")
            .onYes { p ->
                WebForm.send(p)
            }
            .onNo { }
            .send(player)
    }
}
