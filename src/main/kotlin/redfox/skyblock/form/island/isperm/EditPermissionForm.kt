package redfox.skyblock.form.island.isperm

import cn.nukkit.Player
import cn.nukkit.form.element.custom.ElementDropdown
import cn.nukkit.form.element.custom.ElementToggle
import cn.nukkit.form.window.CustomForm
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import redfox.skyblock.data.IslandDB
import redfox.skyblock.utils.IslandUtils

object EditPermissionForm {

    private val gson = Gson()

    fun send(player: Player) {
        val islandDoc = IslandDB.getIsland(player.name) ?: run {
            player.sendMessage("§cAdanız bulunamadı!")
            return
        }

        val permedsJson = islandDoc.getString("permeds") ?: "{}"

        val type = object : TypeToken<MutableMap<String, MutableMap<String, Boolean>>>() {}.type
        val permeds: MutableMap<String, MutableMap<String, Boolean>> =
            gson.fromJson(permedsJson, type) ?: mutableMapOf()

        if (permeds.isEmpty()) {
            player.sendMessage("§cHiç kimseye ada izni vermemişsin!")
            return
        }

        val toggles = IslandUtils.translatePerms
        val playerNames = permeds.keys.toList()

        val form = CustomForm("Ada İzni Düzenle")
        form.addElement(ElementDropdown("Oyuncu Seç:", playerNames))
        form.addElement(ElementToggle("Tüm İzinleri Kaldır", false))
        toggles.forEach { (_, label) ->
            form.addElement(ElementToggle(label, true))
        }

        form.send(player)

        form.onSubmit { p, response ->
            if (response == null) return@onSubmit

            val selectedIndex = response.getDropdownResponse(0).elementId()
            if (selectedIndex !in playerNames.indices) {
                p?.sendMessage("§cGeçersiz oyuncu seçimi!")
                return@onSubmit
            }

            val selectedPlayer = playerNames[selectedIndex]
            val unsetAll = response.getToggleResponse(1)

            if (unsetAll) {
                permeds.remove(selectedPlayer)
                IslandDB.updatePermedsPlayer(p?.name ?: return@onSubmit, gson.toJson(permeds))
                p.sendMessage("§g$selectedPlayer §6adlı oyuncudaki tüm ada izinleri kaldırıldı!")
            } else {
                val perms = permeds[selectedPlayer] ?: mutableMapOf()
                toggles.keys.forEachIndexed { i, key ->
                    val toggleIndex = i + 2
                    if (toggleIndex < response.getResponses().size) {
                        val value = response.getToggleResponse(toggleIndex)
                        perms[key] = value
                    }
                }
                permeds[selectedPlayer] = perms
                IslandDB.updatePermedsPlayer(p.name, gson.toJson(permeds))
                p.sendMessage("§g$selectedPlayer §6isimli oyuncunun ada izinleri düzenlendi.")
            }
        }
    }
}
