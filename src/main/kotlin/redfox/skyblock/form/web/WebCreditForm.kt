package redfox.skyblock.form.web

import cn.nukkit.Player
import cn.nukkit.form.window.ModalForm
import redfox.skyblock.data.Web

object WebCreditForm {

    fun send(player: Player) {
        val db = Web()
        val playerData = db.getPlayer(player.name) ?: run {
            player.sendMessage("§cVeri bulunamadı!")
            return
        }

        val credit = playerData.getInteger("credit", 0)

        ModalForm("Bakiyen")
            .content("Bakiyen: §6${credit}₺")
            .text("§c<- Geri", "Tamam")
            .onYes { p ->
                WebForm.send(p)
            }
            .onNo { }
            .send(player)
    }
}
