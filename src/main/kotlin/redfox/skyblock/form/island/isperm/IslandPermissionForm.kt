package redfox.skyblock.form.island.isperm

import cn.nukkit.Player
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.window.SimpleForm
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import redfox.skyblock.data.IslandDB
import redfox.skyblock.form.island.IslandForm
import redfox.skyblock.utils.IslandUtils

object IslandPermissionForm {

    private const val BUTTON_BACK = "§cGeri"
    private const val BUTTON_PERMITTED_PLAYERS = "Adada İzni Olan Kişiler"
    private const val BUTTON_GIVE_PERMISSION = "Ada İzni Ver"
    private const val BUTTON_EDIT_PERMISSION = "Ada İzni Düzenle"

    fun send(player: Player, isPartner: Boolean) {
        val buttons = buildButtonList(isPartner)
        val form = SimpleForm("Ada İzin")

        buttons.forEach { btnText -> form.addElement(ElementButton(btnText)) }

        form.send(player)

        form.onSubmit { p, response ->
            if (response == null) return@onSubmit
            val selectedButton = response.button().text() ?: return@onSubmit

            when (selectedButton) {
                BUTTON_BACK -> IslandForm.send(p)
                BUTTON_PERMITTED_PLAYERS -> showPermittedPlayers(p, isPartner)
                BUTTON_GIVE_PERMISSION -> openGivePermissionForm(p)
                BUTTON_EDIT_PERMISSION -> openEditPermissionForm(p)
            }
        }
    }

    private fun buildButtonList(isPartner: Boolean): MutableList<String> {
        val list = mutableListOf(BUTTON_BACK, BUTTON_PERMITTED_PLAYERS)
        if (!isPartner) {
            list.add(BUTTON_GIVE_PERMISSION)
            list.add(BUTTON_EDIT_PERMISSION)
        }
        return list
    }

    private fun showPermittedPlayers(player: Player, isPartner: Boolean) {
        val playerName = if (isPartner) IslandDB.getPlayerPartner(player.name) ?: player.name else player.name
        val islandJson = IslandDB.getIsland(playerName) ?: run {
            player.sendMessage("§cAda bilgisine ulaşılamadı!")
            return
        }

        val permedsJson = islandJson.getString("permeds") ?: "{}"
        val permedPlayers = parseJsonObject(permedsJson)

        if (permedPlayers.size() == 0) {
            player.sendMessage("§cHiç bir oyuncuya ada izni vermemişsin!")
            return
        }

        val permsDisplayText = buildPermissionsDisplayText(permedPlayers)
        player.sendMessage("§6Toplamda §g${permedPlayers.size()} §6oyuncuya ada izni bulundu. Oyuncular;\n$permsDisplayText")
    }

    private fun parseJsonObject(jsonString: String): JsonObject {
        return JsonParser.parseString(jsonString).asJsonObject
    }

    private fun buildPermissionsDisplayText(permedPlayers: JsonObject): String {
        val translate = IslandUtils.translatePerms
        val sb = StringBuilder()

        for ((playerKey, permsJson) in permedPlayers.entrySet()) {
            val permsObj = permsJson.asJsonObject
            val permsText = permsObj.entrySet().joinToString(", ") { (permKey, permValue) ->
                val hasPerm = permValue.asBoolean
                if (hasPerm) "§a${translate[permKey]}" else "§c${translate[permKey]}"
            }
            sb.append("§2$playerKey §7( $permsText §7), ")
        }
        return sb.toString().removeSuffix(", ")
    }

    private fun openGivePermissionForm(player: Player) {
        val onlinePlayers = player.server.onlinePlayers.values
            .filter { it.name != player.name }
            .map { it.name }

        if (onlinePlayers.isEmpty()) {
            player.sendMessage("§cSunucuda ada izni verebileceğin kimse yok!")
            return
        }

        GiveIsPermForm.send(player)
    }

    private fun openEditPermissionForm(player: Player) {
        val islandJson = IslandDB.getIsland(player.name) ?: run {
            player.sendMessage("§cAda bilgisine ulaşılamadı!")
            return
        }

        val permedsJson = islandJson.getString("permeds") ?: "{}"
        val permeds = parseJsonObject(permedsJson)

        if (permeds.size() == 0) {
            player.sendMessage("§cHiç kimseye ada izni vermemişsin!")
            return
        }

        EditPermissionForm.send(player)
    }
}
