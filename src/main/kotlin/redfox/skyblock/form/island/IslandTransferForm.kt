package redfox.skyblock.form.island
/*
import cn.nukkit.Player
import cn.nukkit.form.response.CustomResponse
import cn.nukkit.form.window.CustomForm
import cn.nukkit.form.window.ModalForm
import com.google.gson.Gson
import org.bson.Document
import redfox.skyblock.data.IslandDB
import redfox.skyblock.Main
import redfox.skyblock.listener.event.IslandTeleportEvent
import java.io.File

object IslandTransferForm {

    private val gson = Gson()

    fun send(player: Player) {
        val islandData: Document? = IslandDB.getIsland(player.name)
        if (islandData == null) {
            player.sendMessage(Main.TITLE + "§cAdanız bulunamadı!")
            return
        }

        val partners = islandData.getList("partners", String::class.java) ?: emptyList()

        if (partners.isEmpty()) {
            player.sendMessage(Main.TITLE + "§cOrtak bulunamadı!")
            return
        }

        val form = CustomForm(Main.TITLE + " - Ada Devretme")
        form.addElement(ElementDropdown("Ortak Seçiniz:", partners))

        form.send(player)

        form.onSubmit { _: Player?, response: CustomResponse? ->
            if (response == null) return@onSubmit

            val selectedIndex = response.getDropdownResponse(0).elementId()
            if (selectedIndex !in partners.indices) return@onSubmit

            val selectedPartner = partners[selectedIndex]

            player.sendForm(
                ModalForm(
                    title = "Ada Devretme Onayı",
                    content = "Adanızı §a$selectedPartner §7isimli oyuncuya devretmeyi onaylıyor musunuz?",
                    onYes = { p -> performTransfer(p, selectedPartner) },
                    onNo = { p -> send(p) },
                    yesText = "Onaylıyorum",
                    noText = "§cGeri"
                )
            )
        }
    }

    private fun performTransfer(player: Player, selectedPartner: String) {
        val server = player.server
        val selectedPlayer = server.getPlayerExact(selectedPartner)

        if (selectedPlayer == null) {
            player.sendMessage(Main.TITLE + "§cSeçili ortak oyunda değil!")
            return
        }

        val islandData = IslandDB.getIsland(player.name)
        if (islandData == null) {
            player.sendMessage(Main.TITLE + "§cAdanız bulunamadı!")
            return
        }
        val currentPartners = islandData.getList("partners", String::class.java)?.toMutableList() ?: mutableListOf()
        currentPartners.remove(selectedPartner)
        IslandDB.removePartner(player.name, selectedPartner)
        IslandDB.updatePlayerPartners(player.name, currentPartners)
        IslandDB.removeIsland(player.name)
        val playerWorld = server.getLevelByName(player.name)
        val defaultWorld = server.defaultLevel
        playerWorld?.players?.toList()?.forEach { entity ->
            if (entity is Player) {
                entity.teleport(defaultWorld.safeSpawn)
                if (entity.name != player.name) {
                    entity.sendMessage(Main.TITLE + "§cBu ada devredildiği için lobiye aktarıldınız!")
                }
            }
        }
        if (playerWorld != null) {
            server.unloadLevel(playerWorld)
        }
        val dataPath = server.dataPath
        val oldWorldFolder = File(dataPath, "worlds/${player.name}")
        val newWorldFolder = File(dataPath, "worlds/${selectedPartner}")
        oldWorldFolder.renameTo(newWorldFolder)
        IslandDB.addIsland(
            playerName = selectedPartner,
            partners = islandData.getList("partners", String::class.java) ?: emptyList(),
            xp = islandData.getInteger("xp"),
            level = islandData.getInteger("level"),
            visit = islandData.get("visit", Document::class.java)?.toMutableMap() ?: emptyMap(),
            warps = islandData.get("warps", Document::class.java)?.toMutableMap() ?: emptyMap(),
            settings = islandData.get("settings", Document::class.java)?.toMutableMap() ?: emptyMap(),
            blockeds = islandData.getList("blockeds", String::class.java) ?: emptyList(),
            permeds = islandData.getList("permeds", String::class.java) ?: emptyList()
        )

        player.sendMessage(Main.TITLE + "§6Adanız §a$selectedPartner §6isimli oyuncuya devredildi.")
        selectedPlayer.sendMessage(Main.TITLE + "§a${player.name} §6isimli oyuncu adasını sana devretti! Artık ada sahibi sensin.")
        IslandTeleportEvent(selectedPlayer, server.getLevelByName(selectedPartner)).call()
    }
}
*/