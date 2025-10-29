package redfox.skyblock.event.custom

import cn.nukkit.Player
import cn.nukkit.event.Event
import cn.nukkit.event.HandlerList
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Updates.set
import redfox.skyblock.data.IslandDB

class IslandPartnerAddEvent(val player: Player, val partner: Player) : Event() {

    companion object {
        @JvmStatic
        private val handlers = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = handlers
    }

    fun getHandlers(): HandlerList {
        return handlers
    }

    fun call() {
        val partnerIsland = IslandDB.getIsland(partner.name) ?: return
        val partners = partnerIsland.getList("partners", String::class.java)?.toMutableList() ?: mutableListOf()

        if (player.name in partners) return

        partners.add(player.name)

        player.sendMessage("§g${partner.name} §6isimli oyuncuyla ortak oldun!")
        partner.sendMessage("§g${player.name} §6isimli oyuncuyla ortak oldun!")

        IslandDB.collection.updateOne(
            eq("player", partner.name),
            set("partners", partners)
        )

        IslandDB.addPartner(partner.name, player.name)
    }
}
