package redfox.skyblock.event.custom

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.event.Event
import cn.nukkit.event.HandlerList
import redfox.skyblock.data.IslandDB

class IslandPartnerRemoveEvent(
    val playerName: String,
    val partner: Player
) : Event() {

    companion object {
        @JvmStatic
        private val handlers = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = handlers
    }

    fun call() {
        val partnerIsland = IslandDB.getIsland(partner.name)
        val partners = partnerIsland?.getList("partners", String::class.java) ?: listOf()

        if (!partners.contains(playerName)) {
            partner.sendMessage("§cBir hata oluştu!")
            return
        }

        val player = Server.getInstance().getPlayerExact(playerName)
        player?.sendMessage("§g${partner.name} §6isimli oyuncu seni ortaklıktan çıkarttı!")
        partner.sendMessage("§g$playerName §6isimli oyuncu ortaklıktan çıkartıldı.")

        IslandDB.removePartner(playerName, partner.name)
    }
}
