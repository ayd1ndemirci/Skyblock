package redfox.skyblock.form.island

import cn.nukkit.Player
import cn.nukkit.form.window.ModalForm
import redfox.skyblock.event.custom.IslandDeleteEvent

object IslandDeleteForm {

    fun send(player: Player) {
        ModalForm("Ada Sil")
            .content("§3Adanı silmek istiyor musun?\n\n§4§lNOT:§r §cAdanızı sildikten sonra 1 gün boyunca ada oluşturamazsınız!")
            .text("Onaylıyorum", "Geri Dön")
            .onYes { p ->
                IslandDeleteEvent(p).call()
            }
            .onNo { p ->
                IslandForm.send(p)
            }
            .send(player)
    }
}
