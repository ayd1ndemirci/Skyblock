package redfox.skyblock.form.island.partner

import cn.nukkit.Player
import cn.nukkit.form.element.custom.ElementDropdown
import cn.nukkit.form.response.CustomResponse
import cn.nukkit.form.window.CustomForm
import redfox.skyblock.data.IslandDB
import redfox.skyblock.event.custom.IslandPartnerRemoveEvent

object PartnerRemoveForm {

    fun send(player: Player) {
        val island = IslandDB.getIsland(player.name)
        val partners = island?.getList("partners", String::class.java) ?: listOf()

        if (partners.isEmpty()) {
            player.sendMessage("§cOrtakların yok!")
            return
        }

        val form = CustomForm("Ortak Çıkar")
        form.addElement(ElementDropdown("Ortak Seç:", partners))

        form.send(player)

        form.onSubmit { _: Player?, response: CustomResponse? ->
            if (response == null) return@onSubmit

            val selectedIndex = response.getDropdownResponse(0).elementId()
            val selectedPartner = partners.getOrNull(selectedIndex)
            if (selectedPartner == null) {
                player.sendMessage("§cGeçersiz seçim!")
                return@onSubmit
            }

            val event = IslandPartnerRemoveEvent(selectedPartner, player)
            event.call()
        }
    }
}
