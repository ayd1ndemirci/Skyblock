package redfox.skyblock.form.island

import cn.nukkit.Player
import cn.nukkit.form.element.custom.ElementToggle
import cn.nukkit.form.response.CustomResponse
import cn.nukkit.form.window.CustomForm
import org.bson.Document
import redfox.skyblock.data.IslandDB

object IslandSettingsForm {

    fun send(player: Player, partner: Boolean) {
        var playerName = player.name
        if (partner) {
            playerName = IslandDB.getPlayerPartner(playerName) ?: playerName
        }

        val settings: Document = IslandDB.getSettings(playerName)

        val vipFly = settings.getBoolean("vipFly", false)
        val pickup = settings.getBoolean("pickup", false)
        val isLock = settings.getBoolean("isLock", false)

        val form = CustomForm("Ada Ayarları").apply {
            addElement(ElementToggle("VIP'lerin adada uçması", vipFly))
            addElement(ElementToggle("Ziyaretçilerin eşya toplaması", pickup))
            addElement(ElementToggle("Adayı Kilitle", isLock))
        }

        form.send(player)

        form.onSubmit { _: Player?, response: CustomResponse? ->
            if (response == null) return@onSubmit

            val fly = response.getToggleResponse(0)
            val pickupResp = response.getToggleResponse(1)
            val lock = response.getToggleResponse(2)

            player.sendMessage("§aAda ayarların başarıyla güncellendi")

            val newSettings = Document()
            newSettings["vipFly"] = fly
            newSettings["pickup"] = pickupResp
            newSettings["isLock"] = lock

            IslandDB.updatePlayerIslandSettings(playerName, newSettings)
        }
    }
}
