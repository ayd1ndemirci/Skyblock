package redfox.skyblock.form.island.isperm

import cn.nukkit.Player
import cn.nukkit.form.element.custom.ElementDropdown
import cn.nukkit.form.element.custom.ElementToggle
import cn.nukkit.form.window.CustomForm
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import redfox.skyblock.data.IslandDB
import redfox.skyblock.utils.IslandUtils

object GiveIsPermForm {

    private val gson = Gson()

    fun send(player: Player) {
        val onlinePlayers = player.server.onlinePlayers.values
            .filter { it.name != player.name }
            .map { it.name }

        if (onlinePlayers.isEmpty()) {
            player.sendMessage("§cSunucuda ada izni verebileceğin kimse yok!")
            return
        }

        val toggles = IslandUtils.translatePerms

        val form = CustomForm("Ada İzni Ver")

        form.addElement(ElementDropdown("Oyuncu Seç:", onlinePlayers))

        toggles.forEach { (_, label) ->
            form.addElement(ElementToggle(label, true))
        }

        form.onSubmit { p, response ->
            if (p == null || response == null) return@onSubmit

            val selectedIndex = response.getDropdownResponse(0).elementId()
            if (selectedIndex !in onlinePlayers.indices) {
                p.sendMessage("§cGeçersiz oyuncu seçimi!")
                return@onSubmit
            }

            val selectedPlayer = onlinePlayers[selectedIndex]

            val islandDoc = IslandDB.getIsland(p.name)
            val permedsJson = islandDoc?.getString("permeds") ?: "{}"
            val type = object : TypeToken<MutableMap<String, MutableMap<String, Boolean>>>() {}.type
            val permeds: MutableMap<String, MutableMap<String, Boolean>> =
                gson.fromJson(permedsJson, type) ?: mutableMapOf()

            if (permeds.containsKey(selectedPlayer)) {
                p.sendMessage("§4§o$selectedPlayer §r§cisimli oyuncuda ada izni zaten mevcut!")
                return@onSubmit
            }

            val perms = mutableMapOf<String, Boolean>()
            toggles.keys.forEachIndexed { i, key ->
                val toggleIndex = i + 1
                if (toggleIndex < response.getResponses().size) {
                    val value = response.getToggleResponse(toggleIndex)
                    perms[key] = value
                }
            }

            permeds[selectedPlayer] = perms

            IslandDB.updatePermedsPlayer(p.name, gson.toJson(permeds))
            p.sendMessage("§g$selectedPlayer §6isimli oyuncuya ada izni verildi.")
        }

        form.send(player)
    }
}
