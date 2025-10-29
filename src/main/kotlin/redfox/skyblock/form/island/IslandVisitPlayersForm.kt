package redfox.skyblock.form.island

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.window.SimpleForm
import cn.nukkit.level.Location
import redfox.skyblock.data.IslandDB

class IslandVisitPlayersForm(player: Player, partner: Boolean = false) : SimpleForm("Ziyarete Açık Adalar") {

    init {
        val buttons = mutableListOf<ElementButton>()
        val allIslands = IslandDB.collection.find()

        val ownerOrPartner = player.name.let {
            if (partner) IslandDB.getPlayerPartner(it) ?: it else it
        }

        for (doc in allIslands) {
            val islandOwner = doc.getString("player") ?: continue
            if (islandOwner == ownerOrPartner) continue

            val visit = doc.get("visit", org.bson.Document::class.java) ?: continue
            val isOpen = visit.getBoolean("status", false)
            if (isOpen) buttons.add(ElementButton(islandOwner))
        }

        content(if (buttons.isEmpty()) "§cZiyarete Açık Ada Bulunamadı!" else "§7Ziyarete açık adalar listelenmiştir.")
        buttons.forEach { addElement(it) }

        onSubmit { _, response ->
            val selectedName = response.button().text()

            if (!Server.getInstance().isLevelLoaded(selectedName)) {
                Server.getInstance().loadLevel(selectedName)
            }

            val world = Server.getInstance().getLevelByName(selectedName)
            if (world == null) {
                player.sendMessage("§cIşınlanma başarısız oldu.")
                return@onSubmit
            }

            val visitData = IslandDB.getIsland(selectedName)?.get("visit", org.bson.Document::class.java)
            val posString = visitData?.getString("position") ?: "0:64:0"
            val pos = posString.split(":").map { it.toDoubleOrNull() ?: 0.0 }

            val loc = Location(pos[0], pos[1], pos[2], world)
            player.teleport(loc)
            player.sendMessage("§a${selectedName} isimli oyuncunun adasına ışınlandınız.")

            Server.getInstance().getPlayerExact(selectedName)?.sendMessage("§6${player.name} adanı ziyaret ediyor.")
        }
    }
}
