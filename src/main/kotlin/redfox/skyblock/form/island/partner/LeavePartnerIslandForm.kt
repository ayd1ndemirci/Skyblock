package redfox.skyblock.form.island.partner

import cn.nukkit.Player
import cn.nukkit.form.window.ModalForm
import redfox.skyblock.data.IslandDB

object LeavePartnerIslandForm {

    fun send(player: Player) {
        val partnerName = IslandDB.getPlayerPartner(player.name)

        if (partnerName == null) {
            ModalForm("Hata")
                .content("§cSiz adadan ayrılana kadar adam sizi çoktan ortaklıktan çıkartmış bile.. Ooohooo")
                .text("Kapat", "Kapat")
                .onYes { /*  */ }
                .onNo { /*  */ }
                .send(player)
            return
        }

        ModalForm("Adadan Ayrıl")
            .content("$partnerName isimli oyuncunun adasından ayrılmak istediğine emin misin?")
            .text("Evet", "Hayır")
            .onYes { p ->
                val islandDoc = IslandDB.getIsland(partnerName)
                val partnerNames =
                    islandDoc?.getList("partners", String::class.java)?.toMutableList() ?: mutableListOf()
                partnerNames.remove(p.name)
                IslandDB.updatePlayerPartners(partnerName, partnerNames)
                IslandDB.removePartner(p.name, partnerName)
                p.sendMessage("§g$partnerName §6isimli oyuncunun adasından ayrıldınız.")
                val partner = p.server.getPlayerExact(partnerName)
                partner?.sendMessage("§b${p.name} §3isimli oyuncu adanızdan ayrıldı!")
            }
            .onNo { }
            .send(player)
    }
}
