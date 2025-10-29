package redfox.skyblock.utils

import cn.nukkit.Server
import redfox.skyblock.data.IslandDB
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

object IslandUtils {

    val partnershipInvitations: MutableMap<String, MutableList<String>> = mutableMapOf()

    //tüm permleri kapsar, adalara dokunabilir
    const val ISLAND_ADMIN_PERMISSION = "island.admin.permission"

    //eğer bu perm var ise yetkilinin vanishteyken (veyahut normaldeyken) gittiği ada kilitli ise oyuncu engellenmez.
    const val TELEPORT_LOCKED_ISLAND = "island.teleport.lockedIsland"

    //ada tekmeleme, oyuncu engelleme ekranında oyuncu da bu yetki var ise oyuncu gözükmez.
    const val PLAYER_INVISIBLE = "island.playerInvisible"

    val translatePerms: Map<String, String> = mapOf(
        "Place" to "Blok Koyma",
        "Break" to "Blok Kırma",
        "Chest" to "Sandık Açma",
        "PickItem" to "Yerden Eşya Alıp Atma"
    )


    fun createIslandWorld(playerName: String): Boolean {
        try {
            val serverDataPath = Server.getInstance().dataPath
            val sourceDir = File("$serverDataPath/island")
            val destDir = File("$serverDataPath/worlds/$playerName")
            if (!sourceDir.exists() || !sourceDir.isDirectory) return false
            if (destDir.exists()) return false
            destDir.mkdirs()
            copyDirectory(sourceDir, destDir)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    private fun copyDirectory(source: File, target: File) {
        if (source.isDirectory) {
            if (!target.exists()) {
                target.mkdirs()
            }
            source.listFiles()?.forEach { file ->
                val targetFile = File(target, file.name)
                copyDirectory(file, targetFile)
            }
        } else {
            Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
    }

    fun addPartnershipInvitation(player: String, sender: String) {
        val partnerships = partnershipInvitations[player] ?: mutableListOf()
        partnerships.add(sender)
        partnershipInvitations[player] = partnerships
    }

    fun removePartnershipInvitation(player: String, deleted: String) {
        partnershipInvitations[player]?.remove(deleted)
    }

    fun getTopLevel(position: Int = 0): String {
        val players = mutableMapOf<String, Int>()
        val islands = IslandDB.getIslands()
        for (value in islands) {
            val playerName = value["playerName"] as? String ?: continue
            val level = (value["level"] as? Number)?.toInt() ?: continue
            players[playerName] = level
        }

        val sortedPlayers = players.entries.sortedByDescending { it.value }
        val topPlayers = sortedPlayers.map { it.key }
        return if (position > 0 && position <= topPlayers.size) {
            topPlayers[position - 1]
        } else {
            ""
        }
    }
}