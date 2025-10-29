package redfox.skyblock.form.island

import cn.nukkit.Player
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.data.IslandDB
import redfox.skyblock.event.custom.IslandVisitPositionChangeEvent
import redfox.skyblock.event.custom.IslandVisitStatusEvent

class IslandVisitSettingForm(player: Player, partner: Boolean = false) : SimpleForm("Ada Ziyaret Ayarları") {

    init {
        val owner = if (partner) IslandDB.getPlayerPartner(player.name) ?: player.name else player.name
        val island = IslandDB.getIsland(owner)
        val visitDoc = island?.get("visit", org.bson.Document::class.java) ?: org.bson.Document("status", false)
        val isOpen = visitDoc.getBoolean("status", false)

        addElement(ElementButton(if (isOpen) "Ada Ziyaretini Kapat" else "Ada Ziyaretini Aç"))
        addElement(ElementButton("Ada Ziyaret Noktanı Değiştir"))

        onSubmit { _, response ->
            when (response?.buttonId()) {
                0 -> IslandVisitStatusEvent(player, partner).call()
                1 -> IslandVisitPositionChangeEvent(player, partner).execute()
            }
        }
    }
}
